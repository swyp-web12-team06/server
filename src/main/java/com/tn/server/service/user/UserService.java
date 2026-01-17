package com.tn.server.service.user;

import com.tn.server.auth.JwtTokenProvider;
import com.tn.server.domain.user.Role;
import com.tn.server.domain.user.User;
import com.tn.server.dto.user.SignupRequest;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import com.tn.server.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String signup(Long userId, SignupRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이미 가입한 유저인지 검사
        if (user.getRole() == Role.USER) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED);
        }

        if (userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATION);
        }

        user.updateNickname(request.nickname());
        user.agreeToTerms(request.marketingConsent());
        user.upgradeToUser(); // GUEST -> USER로 등업
        user.resetCreatedAt();
        return jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
    }
}