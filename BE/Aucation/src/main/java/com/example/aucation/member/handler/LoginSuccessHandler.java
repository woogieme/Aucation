package com.example.aucation.member.handler;

import java.io.IOException;
import java.time.Duration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.example.aucation.common.error.ApplicationError;
import com.example.aucation.common.error.NotFoundException;
import com.example.aucation.common.jwt.repository.JwtRepository;
import com.example.aucation.common.jwt.service.JwtService;
import com.example.aucation.member.api.dto.LoginResponse;
import com.example.aucation.member.db.entity.Member;
import com.example.aucation.member.db.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {


        String username = extractUsername(authentication);
        Member member = getMemberByUsername(username);
        Long memberPk = member.getId();
        String accessToken = jwtService.issueAccessToken(memberPk);
        String refreshToken = jwtService.issueRefreshToken();
        log.info("A"+member.getMemberRefresh());
        member.updateRefreshToken(refreshToken);
        log.info("B"+member.getMemberRefresh());
        memberRepository.saveAndFlush(member);

        jwtService.setAccessAndRefreshToken(response, accessToken, refreshToken);
        setResponseBody(response, member);
        log.info("로그인에 성공하였습니다. 아이디 : {} AccessToken : {}", username, accessToken);
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private Member getMemberByUsername(String username) {
        return memberRepository.findByMemberId(username)
                .orElseThrow(() -> new NotFoundException(ApplicationError.MEMBER_NOT_FOUND));
    }

    private void setResponseBody(HttpServletResponse response, Member member) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(LoginResponse.builder()
                .role(member.getMemberRole().getKey())
                .socialType(member.getSocialType().name())
                .build()));
    }
}
