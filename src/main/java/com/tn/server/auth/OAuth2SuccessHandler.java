package com.tn.server.auth;

import com.tn.server.auth.repository.RefreshTokenRepository;
import com.tn.server.domain.user.Role;
import com.tn.server.domain.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.frontend.redirect-uri}")
    private String frontendUri;

    @Value("${app.cookie.secure}")
    private boolean secure;

    @Value("${app.cookie.same-site}")
    private String sameSite;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken));

        addRefreshTokenCookie(response, refreshToken);
        boolean isNewUser = (user.getRole() == Role.GUEST);

        // 프론트엔드로 redirect
        // Access Token은 프론트가 페이지 로드 후 /reissue API로 받아갈 것
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUri)
                .queryParam("isNewUser", isNewUser)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .sameSite(sameSite)      // prod: None, 서로 다른 도메인(3000 <-> 8080) 간 쿠키 전송 허용, local: Lax
                .httpOnly(true)
                .secure(secure)          // local: false, prod: true
                .maxAge(60 * 60 * 24 * 14) // 2주
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
