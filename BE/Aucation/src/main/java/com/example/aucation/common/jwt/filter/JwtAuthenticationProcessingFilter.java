package com.example.aucation.common.jwt.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.BadRequestException;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.common.jwt.service.JwtService;
import com.example.aucation.common.util.PasswordGenerator;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.entity.SocialType;
import com.example.aucation.member.db.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private static final String CONTEXT_PATH="/api/v1";

	private static final String SIGNUP_URI = "/members/signup";

	private static final String REISSUE_URI = "/api/v1/members/reissue";

	private final JwtService jwtService;

	private final MemberRepository memberRepository;

	private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Value("${jwt.tokenAuthorizationWhiteList}")
	private List<String> tokenAUthorizationWhiteList;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		if(isNotRequiredJwtAuthencation(request)){
			filterChain.doFilter(request,response);
			return;
		}
		String accessToken = jwtService.extractAccessToken(request);
		String refreshToken = jwtService.extractRefreshToken(request);

		if(!request.getRequestURI().equals(REISSUE_URI)){
			jwtService.extractMemberPk(accessToken);
		}
		if(refreshToken !=null && request.getRequestURI().equals(REISSUE_URI)){

			Long memberPk = jwtService.extractMemberPkFromExpiredToken(accessToken);
			jwtService.validateRefreshToken(refreshToken,memberPk);
			reissueAccessToken(response,memberPk);
			return;
		}
		saveAccessTokenAuthentication(request,response,filterChain,accessToken);
	}

	private void saveAccessTokenAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String accessToken) throws
		ServletException,
		IOException {
		Member member = memberRepository.findById(jwtService.extractMemberPk(accessToken))
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		saveAuthentication(member);
		filterChain.doFilter(request, response);
	}

	private void reissueAccessToken(HttpServletResponse response, Long memberPk) {
		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
		jwtService.setAccessAndRefreshToken(response, jwtService.issueAccessToken(member.getId()),
			jwtService.reissueRefreshToken(memberPk));
		
	}

	private boolean isNotRequiredJwtAuthencation(HttpServletRequest request) {
		return tokenAUthorizationWhiteList.stream().anyMatch(uri -> request.getRequestURI().contains(uri))
			||(request.getRequestURI().equals("/api/v1/members/mainPage")) ||(request.getRequestURI().equals(CONTEXT_PATH + SIGNUP_URI) && request.getMethod().equals(HttpMethod.POST.name()));

	}

	private void saveAuthentication(Member member)  {
		String password = member.getMemberPw();
		if (member.getSocialType() != SocialType.NORMAL) {
			password = PasswordGenerator.generate();
		}
		if (password == null) {
			throw new BadRequestException(ApplicationError.BAD_MEMBER);
		}
		UserDetails userDetails = User.builder()
			.username(member.getId().toString())
			.password(password)
			.roles(member.getMemberRole().name())
			.build();
		Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
			userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
