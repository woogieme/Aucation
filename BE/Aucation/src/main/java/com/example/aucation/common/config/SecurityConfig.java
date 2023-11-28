package com.example.aucation.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.aucation.common.filter.ExceptionHandlerFilter;
import com.example.aucation.common.jwt.filter.JwtAuthenticationProcessingFilter;
import com.example.aucation.common.jwt.service.JwtService;
import com.example.aucation.member.api.service.LoginService;
import com.example.aucation.member.db.repository.MemberRepository;
import com.example.aucation.member.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.example.aucation.member.handler.LoginFailureHandler;
import com.example.aucation.member.handler.LoginSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	private final ObjectMapper objectMapper;
	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final LoginService loginService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.formLogin().disable()
			.httpBasic().disable()
			.csrf().disable()
			.cors().configurationSource(corsConfiguration())
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.mvcMatchers("/").permitAll()
			.mvcMatchers("/docs/index.html").permitAll()
			.mvcMatchers(HttpMethod.POST,"/api/v1/members").permitAll()
			.mvcMatchers(HttpMethod.GET,"/api/v1/members/mainPage").permitAll()
			.mvcMatchers("/api/v1/members/signup").permitAll()
			.mvcMatchers("/api/v1/members/login").permitAll()
			.mvcMatchers(HttpMethod.GET,"/api/v1/members/verification/**").permitAll()
			.mvcMatchers(HttpMethod.GET,"/api/v1/members/certification/**").permitAll()
			.mvcMatchers("/ws/**").permitAll()
			.mvcMatchers("/auc-server/**").permitAll()
			.anyRequest().authenticated()
			.and()
			.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
			.addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(exceptionHandlerFilter(), JwtAuthenticationProcessingFilter.class)
			.build();
	}

	@Bean
	public CorsConfigurationSource corsConfiguration() {
		CorsConfiguration configuration =new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("https://localhost:8001","https://localhost:3000","http://localhost:3000","http://localhost:8001","https://aucation.co.kr","https://www.aucation.co.kr"));
		configuration.addAllowedHeader("*");
		configuration.setAllowedMethods(List.of(
			HttpMethod.DELETE.name(),HttpMethod.GET.name(),HttpMethod.PATCH.name(),HttpMethod.PUT.name(),HttpMethod.POST.name(),HttpMethod.OPTIONS.name()
		));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(List.of("Authorization","Authorization-refresh"));
		UrlBasedCorsConfigurationSource source =new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**",configuration);
		return source;
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(loginService);
		return new ProviderManager(provider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter(){
		CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
			= new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
		customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
		customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
		customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
		return customJsonUsernamePasswordLoginFilter;
	}
	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
		return new JwtAuthenticationProcessingFilter(jwtService, memberRepository);
	}

	@Bean
	public LoginSuccessHandler loginSuccessHandler() {
		return new LoginSuccessHandler(jwtService, memberRepository, objectMapper);
	}

	@Bean
	public LoginFailureHandler loginFailureHandler() {
		return new LoginFailureHandler();
	}

	@Bean
	public ExceptionHandlerFilter exceptionHandlerFilter() {
		return new ExceptionHandlerFilter(objectMapper);
	}
}
