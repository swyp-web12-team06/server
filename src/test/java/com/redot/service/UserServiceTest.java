package com.redot.service;

import com.redot.auth.JwtTokenProvider;
import com.redot.auth.repository.RefreshTokenRepository;
import com.redot.domain.user.Role;
import com.redot.domain.user.User;
import com.redot.dto.user.SellerUpgradeRequest;
import com.redot.dto.user.SignupRequest;
import com.redot.dto.user.UserUpdateRequest;
import com.redot.dto.user.UserProfileResponse;

import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.PromptRepository;

import com.redot.repository.user.UserRepository;
import com.redot.service.image.ImageManager;
import com.redot.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    PromptRepository promptRepository;

    @Mock
    ImageManager imageManager;

    // 회원가입 (signup) 테스트

    @Test
    @DisplayName("회원가입 성공: GUEST -> USER 등업 및 토큰 발급")
    void signup_success() {
        // given
        Long userId = 100L;
        User user = User.builder().email("test@test.com").role(Role.GUEST).build();
        SignupRequest request = new SignupRequest("newNick", true,true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname("newNick")).willReturn(false);
        given(jwtTokenProvider.createAccessToken(any(), any())).willReturn("access-token");

        // when
        String token = userService.signup(userId, request);

        // then
        assertThat(user.getRole()).isEqualTo(Role.USER); // 등업 확인
        assertThat(user.getNickname()).isEqualTo("newNick"); // 닉네임 변경 확인
        assertThat(token).isEqualTo("access-token"); // 토큰 반환 확인
    }

    @Test
    @DisplayName("회원가입 실패: 이미 가입된 유저(USER)인 경우")
    void signup_fail_already_registered() {
        // given
        Long userId = 1L;
        User user = User.builder().role(Role.USER).build(); // 이미 USER
        SignupRequest request = new SignupRequest("nick", true,false);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.signup(userId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_REGISTERED);
    }

    @Test
    @DisplayName("회원가입 실패: 닉네임 중복")
    void signup_fail_nickname_duplication() {
        // given
        Long userId = 1L;
        User user = User.builder().role(Role.GUEST).build();
        SignupRequest request = new SignupRequest("dupNick", true,true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname("dupNick")).willReturn(true); // 중복 발생

        // when & then
        assertThatThrownBy(() -> userService.signup(userId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_DUPLICATION);
    }

    // 회원탈퇴 (withdraw) 테스트

    @Test
    @DisplayName("회원탈퇴 성공: 관련 데이터 삭제 및 Soft Delete 처리")
    void withdraw_success() {
        // given
        Long userId = 1L;
        User user = User.builder().role(Role.USER).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.withdraw(userId, "Just leaving");

        // then
        verify(promptRepository).softDeleteAllByUserId(userId); // 프롬프트 삭제 호출 확인
        verify(refreshTokenRepository).deleteByUserId(userId); // 토큰 삭제 호출 확인
        assertThat(user.getDeletedAt()).isNotNull(); // 유저가 삭제 상태로 변했는지 확인
    }

    @Test
    @DisplayName("회원탈퇴 실패: 이미 탈퇴한 회원")
    void withdraw_fail_already_deleted() {
        // given
        Long userId = 1L;
        User user = User.builder().build();
        user.withdraw("Just"); // 이미 탈퇴 상태로 만듦

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.withdraw(userId, "reason"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_DELETED);
    }

    // 프로필 수정 (updateProfile) 테스트

    @Test
    @DisplayName("프로필 수정: 이미지가 변경되면 기존 이미지는 삭제되어야 한다")
    void updateProfile_deleteOldImage() {
        // given
        Long userId = 1L;
        String oldKey = "uploads/1/old.jpg";
        String newKey = "uploads/1/new.jpg";

        User user = User.builder().email("test@a.com").role(Role.USER).build();
        ReflectionTestUtils.setField(user, "profileImageKey", oldKey);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserUpdateRequest request = new UserUpdateRequest("newNick", "bio", newKey);

        // when
        userService.updateProfile(userId, request);

        // then
        verify(imageManager, times(1)).delete(oldKey, false);
        assertThat(user.getProfileImageKey()).isEqualTo(newKey);
    }

    @Test
    @DisplayName("프로필 수정 실패: 변경하려는 닉네임이 중복됨")
    void updateProfile_fail_nickname_duplicate() {
        // given
        Long userId = 1L;
        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "nickname", "oldNick"); // 기존 닉네임 설정

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname("duplicateNick")).willReturn(true); // 중복 설정

        UserUpdateRequest request = new UserUpdateRequest("duplicateNick", null, "bio");

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(userId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_DUPLICATION);
    }

    // 프로필 조회 (getMyProfile) 테스트
    @Test
    @DisplayName("내 프로필 조회: Key가 URL로 변환되어 반환된다")
    void getMyProfile_success() {
        // given
        Long userId = 1L;
        String imageKey = "uploads/1/img.png";
        String expectedUrl = "https://pub-r2-domain.com/uploads/1/uuid-img.png";

        User user = User.builder().role(Role.USER).build();
        ReflectionTestUtils.setField(user, "profileImageKey", imageKey);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(imageManager.getPublicUrl(imageKey)).willReturn(expectedUrl);

        // when
        UserProfileResponse response = userService.getMyProfile(userId);

        // then
        assertThat(response.profileImageUrl()).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("판매자 등업 성공: Role 변경 및 약관 동의 시간 저장")
    void upgradeToSeller_success() {
        // given
        Long userId = 1L;
        User user = User.builder().role(Role.USER).build(); // 일반 유저
        SellerUpgradeRequest request = new SellerUpgradeRequest(true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.upgradeToSeller(userId, request);

        // then
        assertThat(user.getRole()).isEqualTo(Role.SELLER); // 역할 변경 확인
        assertThat(user.getSellerTermsAgreed()).isTrue(); // 동의 여부 확인
        assertThat(user.getSellerTermsAgreedAt()).isNotNull(); // 시간 저장 확인
    }

    @Test
    @DisplayName("판매자 등업 실패: 이미 판매자임")
    void upgradeToSeller_fail_already_seller() {
        // given
        Long userId = 1L;
        User user = User.builder().role(Role.SELLER).build(); // 이미 판매자
        SellerUpgradeRequest request = new SellerUpgradeRequest(true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.upgradeToSeller(userId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_SELLER);
    }

    @Nested
    @DisplayName("유저 조회 (findActiveUser)")
    class FindActiveUserTest {

        @Test
        @DisplayName("성공: 존재하는 활성 유저를 조회하면 User를 반환한다")
        void success() {
            // given
            Long userId = 1L;
            User activeUser = User.builder().role(Role.USER).build();
            ReflectionTestUtils.setField(activeUser, "id", userId);
            ReflectionTestUtils.setField(activeUser, "deletedAt", null);

            given(userRepository.findById(userId)).willReturn(Optional.of(activeUser));

            // when
            User result = userService.findActiveUser(userId);

            // then
            assertThat(result).isEqualTo(activeUser);
        }

        @Test
        @DisplayName("실패: 유저가 존재하지 않으면 예외가 발생한다")
        void fail_notFound() {
            // given
            Long userId = 999L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.findActiveUser(userId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 탈퇴한 유저(deletedAt 존재)는 예외가 발생한다")
        void fail_deletedUser() {
            // given
            Long userId = 2L;
            User deletedUser = User.builder().role(Role.USER).build();
            ReflectionTestUtils.setField(deletedUser, "id", userId);

            // 탈퇴 날짜 설정 (탈퇴함)
            ReflectionTestUtils.setField(deletedUser, "deletedAt", Instant.now());

            given(userRepository.findById(userId)).willReturn(Optional.of(deletedUser));

            // when & then
            assertThatThrownBy(() -> userService.findActiveUser(userId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }
}