package com.example.aucation.common.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.aucation.common.jwt.service.JwtService;
import com.example.aucation.common.support.AuthorizedVariable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthorizationResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;
    private static final String BEARER = "Bearer ";
    private static final String REMOVE = "";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthorizedVariable.class);
    }

    @Override
    public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String Authorization_token = request.getHeader("Authorization");
        String accessToken = Authorization_token.replace(BEARER, REMOVE);
        return jwtService.extractMemberPk(accessToken);
    }
}