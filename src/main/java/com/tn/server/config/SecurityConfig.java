package com.tn.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 보안 설정
                .csrf(AbstractHttpConfigurer::disable)

                // 2. H2 콘솔 화면 깨짐 방지 설정
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                // 3. 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/h2-console/**").permitAll() // 이 두 곳은 프리패스!
                        .anyRequest().permitAll() // 나머지는 다 검사해!
                );

        return http.build();
    }
}