package com.example.aucation.common.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.aucation.common.resolver.AuthorizationResolver;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class AuthorizationConfig implements WebMvcConfigurer {

    private final AuthorizationResolver authorizationResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authorizationResolver);
    }
}