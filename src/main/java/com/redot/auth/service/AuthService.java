package com.redot.auth.service;

import com.redot.auth.JwtTokenProvider;
import com.redot.auth.RefreshToken;
import com.redot.auth.dto.TokenReissueResponse;
import com.redot.auth.repository.RefreshTokenRepository;
import com.redot.domain.user.Role;
import com.redot.domain.user.User;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public TokenReissueResponse reissue(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN); //401
        }

        // DB에 저장된 리프레시 토큰 가져오기
        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_LOGOUT)); // DB에 없으면 이미 로그아웃 상태

        Long tokenUserId = jwtTokenProvider.getUserId(refreshToken);
        if (!savedToken.getUserId().equals(tokenUserId)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN); //401
        }

        if (savedToken.isExpired()) {
            refreshTokenRepository.delete(savedToken); // 만료 토큰이면 DB에서 삭제
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(savedToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)); //404

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        boolean isNewUser = (user.getRole() == Role.GUEST);

        return new TokenReissueResponse(
                newAccessToken,
                isNewUser,
                user.getRole().name()
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        // 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // DB에 해당 토큰 없으면 이미 로그아웃 된 상태
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_LOGOUT));

        refreshTokenRepository.delete(token);

    }
}