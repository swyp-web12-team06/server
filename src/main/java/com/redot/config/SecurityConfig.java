package com.redot.config;

import com.redot.auth.*;
import com.redot.auth.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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

    @Value("${security.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)//CSRF 보안 비활성화 (JWT 쓸 땐 필요 없음)
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
                        .requestMatchers("/health", "/h2-console/**", "/error", "/favicon.ico",
                                "/login/**", "/oauth2/**", "/dev/**",
                                "/payment-test.html", "/payment-test.css").permitAll()

                        // Swagger 엔드포인트 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/callback/kie-ai").permitAll()
                        .requestMatchers("/auth/reissue").permitAll()
                        .requestMatchers("/auth/logout").authenticated()

                        .requestMatchers("/user/signup").hasRole("GUEST")
                        .requestMatchers("/user/me/library/sales").hasRole("SELLER")
                        .requestMatchers("/user/me/library/purchase").hasAnyRole("USER", "SELLER")
                        .requestMatchers("/user/me/**").hasAnyRole("USER", "SELLER")
                        .requestMatchers(HttpMethod.POST, "/user/upgrade-seller").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/user/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/product/user/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/product/*/purchase").hasAnyRole("USER", "SELLER")

                        .requestMatchers(HttpMethod.GET, "/product/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/product/*/generate").hasAnyRole("USER", "SELLER")
                        .requestMatchers(HttpMethod.POST, "/product/*/estimate").hasAnyRole("USER", "SELLER")
                        .requestMatchers(HttpMethod.POST, "/product/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PATCH, "/product/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/product/**").hasRole("SELLER")

                        .requestMatchers(HttpMethod.GET, "/metadata/**").permitAll()
                        .requestMatchers("/credit/options").permitAll()
                        .requestMatchers("/credit/**").hasAnyRole("USER", "SELLER")

                        .requestMatchers(HttpMethod.GET, "/image/*/status").hasAnyRole("USER", "SELLER")
                        .requestMatchers(HttpMethod.GET, "/image/*/download").hasAnyRole("USER", "SELLER")
                        .requestMatchers(HttpMethod.GET, "/image/presigned-upload").hasAnyRole("USER", "SELLER")
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 쿠키나 인증 정보를 포함
        configuration.setExposedHeaders(List.of("Authorization"));  // 브라우저에 노출할 헤더

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}