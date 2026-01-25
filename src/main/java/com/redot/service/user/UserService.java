package com.redot.service.user;

import com.redot.auth.JwtTokenProvider;
import com.redot.auth.repository.RefreshTokenRepository;
import com.redot.domain.user.Role;
import com.redot.domain.user.User;
import com.redot.dto.user.PublicUserProfileResponse;
import com.redot.dto.user.SignupRequest;
import com.redot.dto.user.UserProfileResponse;
import com.redot.dto.user.UserUpdateRequest;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.PromptRepository;
import com.redot.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PromptRepository promptRepository;

    @Transactional
    public String signup(Long userId, SignupRequest request) {
        User user = findActiveUser(userId);

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

    @Transactional
    public void withdraw(Long userId, String deleteReason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이미 탈퇴한 회원인지 확인
        if (user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.ALREADY_DELETED);
        }

        // 판매중인 Prompt 일괄 삭제. is_deleted=true
        promptRepository.softDeleteAllByUserId(userId);

        // userId로 refresh token 찾아서 삭제 (모든 기기)
        refreshTokenRepository.deleteByUserId(userId);

        user.withdraw(deleteReason);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
        User user = findActiveUser(userId);

        return UserProfileResponse.from(user);
    }

    @Transactional
    public void updateProfile(Long userId, UserUpdateRequest request) {
        User user = findActiveUser(userId);

        // 닉네임 변경 시 중복 체크
        // 요청 닉네임 존재 && 기존 닉네임과 다를 때만 체크
        if (request.nickname() != null && !request.nickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.nickname())) {
                throw new BusinessException(ErrorCode.NICKNAME_DUPLICATION);
            }
        }

        user.updateProfile(
                request.nickname(),
                request.profileImageUrl(),
                request.bio()
        );
    }

    @Transactional(readOnly = true)
    public PublicUserProfileResponse getPublicProfile(Long userId) {
        User user = findActiveUser(userId);

        return PublicUserProfileResponse.from(user);
    }

    // 다른 서비스에서도 사용하기 위해 public으로 변경
    public User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 탈퇴한 유저
        if (user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }
}