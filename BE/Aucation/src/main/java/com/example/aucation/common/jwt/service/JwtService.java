package com.example.aucation.common.jwt.service;

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.JwtException;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.repository.MemberRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Getter
public class JwtService {

	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";

	private static final String MEMBER_PK_CLAIM = "MemberPk";
	private static final String BEARER = "Bearer ";
	private static final String REMOVE = "";
	private static final String ACCESS_TOKEN_HEADER = "Authorization";
	private static final String REFRESH_TOKEN_HEADER = "Authorization-refresh";

	private final MemberRepository memberRepository;

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.accessToken.expiration}")
	private Long accessTokenExpirationPeriod;

	@Value("${jwt.refreshToken.expiration}")
	private Long refreshTokenExpirationPeriod;

	public String extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(ACCESS_TOKEN_HEADER))
			.map(accessToken -> {
				validateTokenType(accessToken);
				return accessToken.replace(BEARER, REMOVE);
			})
			.orElseThrow(() -> new JwtException(ApplicationError.UNAUTHORIZED_MEMBER));
	}

	public String extractRefreshToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(REFRESH_TOKEN_HEADER))
			.map(refreshToken -> {
				validateTokenType(refreshToken);
				return refreshToken.replace(BEARER, REMOVE);
			})
			.orElse(null);
	}

	private void validateTokenType(String token) {
		if (!token.startsWith(BEARER)) {
			throw new JwtException(ApplicationError.INVALID_TOKEN_TYPE);
		}
	}

	public Long extractMemberPk(String accessToken) {
		try {
			return JWT.require(Algorithm.HMAC512(secretKey))
				.build()
				.verify(accessToken)
				.getClaim(MEMBER_PK_CLAIM)
				.asLong();
		} catch (Exception e) {
			throw new JwtException(ApplicationError.INVALID_ACCESS_TOKEN);
		}
	}

	public Long extractMemberPkFromExpiredToken(String accessToken) {
		try {
			DecodedJWT jwt = JWT.decode(accessToken);
			// JWT에서 memberPk를 추출
			Long memberPk = jwt.getClaim(MEMBER_PK_CLAIM).asLong();
			return memberPk;
		} catch (JWTDecodeException e) {
			throw new JwtException(ApplicationError.INVALID_ACCESS_TOKEN);
		}
	}

	public void validateRefreshToken(String token, Long memberPk) {
		log.info(token);
		try {
			JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
			checkRefreshTokenExist(memberPk);
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new JwtException(ApplicationError.INVALID_REFRESH_TOKEN);
		}

	}

	public boolean validateAccessToken(String token){
		log.info(token);
		try {
			JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new JwtException(ApplicationError.INVALID_REFRESH_TOKEN);
		}

		return false;
	}

	private void checkRefreshTokenExist(Long memberPk) {
		log.info(String.valueOf(memberPk));
		Member member = memberRepository.findById(memberPk)
			.orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));

		if (member.getMemberRefresh() == null) {

			throw new JwtException(ApplicationError.UNAUTHORIZED_MEMBER);
		}
	}

	public void setAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader(ACCESS_TOKEN_HEADER, accessToken);
		response.setHeader(REFRESH_TOKEN_HEADER, refreshToken);
	}

	public String reissueRefreshToken(Long memberPk) {
		String reissuedRefreshToken = issueRefreshToken();
		Member member = memberRepository.findById(memberPk).orElseThrow(() -> new NotFoundException(
			ApplicationError.MEMBER_NOT_FOUND));
		member.setMemberRefresh(reissuedRefreshToken);
		memberRepository.save(member);
		return reissuedRefreshToken;
	}

	public String issueAccessToken(Long memberPk) {
		return JWT.create()
			.withSubject(ACCESS_TOKEN_SUBJECT)
			.withExpiresAt(new Date(new Date().getTime() + accessTokenExpirationPeriod))
			.withClaim(MEMBER_PK_CLAIM, memberPk)
			.sign(Algorithm.HMAC512(secretKey));

	}

	public String issueRefreshToken() {
		log.info(String.valueOf(refreshTokenExpirationPeriod));
		return JWT.create()
			.withSubject(REFRESH_TOKEN_SUBJECT)
			.withExpiresAt(new Date(new Date().getTime() + refreshTokenExpirationPeriod))
			.sign(Algorithm.HMAC512(secretKey));
	}

}
