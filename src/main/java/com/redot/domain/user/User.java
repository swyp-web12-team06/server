package com.redot.domain.user;

import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import java.time.temporal.ChronoUnit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = true)
    private String email;

    @Column(unique = true, length = 15)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer warningCount = 0;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isBanned = false;

    @Column(name = "profile_image_key")
    private String profileImageKey;

    @Column(length = 200)
    private String bio;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "delete_reason", length = 300)
    private String deleteReason;

    @Column(name = "credit_balance", nullable = false)
    @ColumnDefault("0")
    private Integer creditBalance = 0;

    @Column
    private Instant nicknameUpdatedAt;

    @Column(nullable = true)
    private String provider;

    @Column(nullable = true)
    private String providerId;

    @Column(nullable = false)
    private Boolean termsAgreed = false;

    @Column
    private Instant termsAgreedAt;

    @Column(nullable = false)
    private Boolean marketingConsent = false;

    @Column
    private Instant marketingConsentedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = true)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣_-]{2,15}$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEX);


     @Builder
     public User(String email, String profileImageKey, Role role, String provider, String providerId) {
         this.email = email;
         this.profileImageKey = profileImageKey;
         this.role = role;
         this.provider = provider;
         this.providerId = providerId;
     }

    public void updateNickname(String newNickname) {
        if (newNickname == null || newNickname.isBlank()) {
            throw new BusinessException(ErrorCode.NICKNAME_MISSING);
        }

        if (this.nickname != null && this.nickname.equals(newNickname)) {
            return; // 기존 닉네임과 같으면 아무 일도 하지 않음
        }

        // 닉네임은 30일에 한 번만 변경 가능
        if (this.nicknameUpdatedAt != null) {
            // 마지막 변경일과 현재 시간 사이의 날짜 차이 계산
            long daysSinceLastUpdate = ChronoUnit.DAYS.between(this.nicknameUpdatedAt, Instant.now());

            if (daysSinceLastUpdate < 30) {
                throw new BusinessException(ErrorCode.NICKNAME_UPDATE_LIMIT_EXCEEDED);
            }
        }

        if (!NICKNAME_PATTERN.matcher(newNickname).matches()) {
            throw new BusinessException(ErrorCode.NICKNAME_INVALID_FORMAT);
        }

        this.nickname = newNickname;
        this.nicknameUpdatedAt = Instant.now();
    }

    public void updateProfile(String nickname, String profileImageKey, String bio) {
        if (nickname != null) {
            this.updateNickname(nickname);
        }

        if (profileImageKey != null) { //빈 문자열 ""이 오면 삭제로 간주
            this.profileImageKey = profileImageKey.isEmpty() ? null : profileImageKey;
        }

        if (bio != null) { //빈 문자열 ""이 오면 삭제로 간주
            if (bio.length() > 200) {
                throw new BusinessException(ErrorCode.BIO_TOO_LONG);
            }
            this.bio = bio.isEmpty() ? null : bio;
        }
    }

    public User updateEmail(String email) {
        this.email = email;
        return this;
    }

    public void resetCreatedAt() {
        this.createdAt = Instant.now();
    }

    public void upgradeToUser() {
        this.role = Role.USER;
    }
    public void upgradeToSeller(){this.role=Role.SELLER; }

    public void agreeToTerms(boolean marketingConsent) {
        this.termsAgreed = true;
        this.termsAgreedAt = Instant.now();
        this.marketingConsent = marketingConsent;
        if (marketingConsent) {
            this.marketingConsentedAt = Instant.now();
        }
    }

    public void updateMarketingConsent(boolean consent) {
        this.marketingConsent = consent;

        if (consent) {
            this.marketingConsentedAt = Instant.now();
        } else {
            this.marketingConsentedAt = null;
        }
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    // 크레딧 충전 메서드
    public void addCredit(int amount) {
        this.creditBalance += amount;
    }

    // 잔액 차감 로직 (비즈니스 메서드)
    public void decreaseCredit(int amount) {
        if (this.creditBalance < amount) {
            // 명세서에 있는 400 에러 코드 적용
            throw new BusinessException(ErrorCode.INSUFFICIENT_CREDIT);
        }
        this.creditBalance -= amount;
    }

    //회원 탈퇴
    public void withdraw(String deleteReason) {
        this.deleteReason = deleteReason;
        this.deletedAt = Instant.now();

        this.role = Role.GUEST;
        this.email = null;
        this.nickname = null;
        this.profileImageKey = null;
        this.bio = null;
        this.nicknameUpdatedAt = null;
        this.creditBalance = 0;
        this.termsAgreed = false;
        this.termsAgreedAt = null;
        this.marketingConsent = false;
        this.marketingConsentedAt = null;
        this.provider = null;
        this.providerId = null;
    }

    public void clearDeletedAt() {
        this.deletedAt=null;
    }
}
