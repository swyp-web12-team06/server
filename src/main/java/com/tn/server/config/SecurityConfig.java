package com.tn.server.config;

import com.tn.server.auth.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //CSRF 보안 비활성화 (JWT 쓸 땐 필요 없음)
                .httpBasic(AbstractHttpConfigurer::disable) //ID/PW 직접 인증 안 씀
                .formLogin(AbstractHttpConfigurer::disable) //로그인 폼 안 씀

                // H2 콘솔 화면 깨짐 방지 설정
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                //세션 사용 안 함 (STATELESS 모드)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/h2-console/**", "/favicon.ico", "/favicon.png", "/error",
                                "/login/**", "/oauth2/**", "/dev/**",
                                "/payment-test.html", "/payment-test.css", "/shoes.png").permitAll()
                        .requestMatchers("/credit/options").permitAll() // 결제 옵션 조회 허용
                        .requestMatchers("/auth/reissue").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/{userId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/product/**").permitAll()
                        .requestMatchers("/user/me/**").hasRole("USER")
                        .requestMatchers("/credit/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/image/{imageId}/download").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/product/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/product/**").hasRole("USER")
                        .requestMatchers("/user/signup", "/auth/logout").authenticated()
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) //401
                        .accessDeniedHandler(customAccessDeniedHandler) //403
                )
                // 소셜 로그인 설정
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                //필터 순서 설정
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}