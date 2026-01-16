package com.tn.server.auth.service;

import com.tn.server.auth.JwtTokenProvider;
import com.tn.server.auth.dto.TokenReissueResponse;
import com.tn.server.domain.user.Role;
import com.tn.server.domain.user.User;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import com.tn.server.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public TokenReissueResponse reissue(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        boolean isNewUser = (user.getRole() == Role.GUEST);

        return new TokenReissueResponse(
                newAccessToken,
                isNewUser,
                user.getRole().name()
        );
    }
}