package com.tn.server.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 상태 코드 401 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 응답 형식 JSON 설정
        response.setContentType("application/json;charset=UTF-8");

        // JSON 응답 본문 작성
        response.getWriter().write("{\"code\": \"UNAUTHORIZED\", \"message\": \"로그인이 필요합니다.\"}");
    }
}