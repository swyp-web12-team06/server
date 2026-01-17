package com.tn.server.domain.user;

import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(nullable = false)
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

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    @Column(length = 200)
    private String bio;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted = false;

    @Column(name = "credit_balance", nullable = false)
    @ColumnDefault("0")
    private Integer creditBalance = 0;

    @Column
    private Instant nicknameUpdatedAt;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
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
    public User(String email, String profileImageUrl, Role role, String provider, String providerId) {
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void updateNickname(String newNickname) {
        if (newNickname == null || newNickname.isBlank()) {
            throw new BusinessException(ErrorCode.NICKNAME_MISSING);
        }

        if (this.nickname.equals(newNickname)) {
            return; // 기존 닉네임과 같으면 아무 일도 하지 않음
        }

        if (!NICKNAME_PATTERN.matcher(newNickname).matches()) {
            throw new BusinessException(ErrorCode.NICKNAME_INVALID_FORMAT);
        }

        this.nickname = newNickname;
        this.nicknameUpdatedAt = Instant.now();
    }

    public void updateProfile(String nickname, String profileImageUrl, String bio) {
        if (nickname != null) {
            this.updateNickname(nickname);
        }

        if (profileImageUrl != null) { //빈 문자열 ""이 오면 삭제로 간주
            this.profileImageUrl = profileImageUrl.isEmpty() ? null : profileImageUrl;
        }

        if (bio != null) { //빈 문자열 ""이 오면 삭제로 간주
            if (bio.length() > 200) {
                throw new BusinessException(ErrorCode.BIO_TOO_LONG);
            }
            this.bio = bio.isEmpty() ? null : bio;
        }
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

    // 잔액 차감 로직 (비즈니스 메서드)
    public void decreaseCredit(int amount) {
        if (this.creditBalance < amount) {
            // 명세서에 있는 400 에러 코드 적용
            throw new BusinessException(ErrorCode.INSUFFICIENT_CREDIT);
        }
        this.creditBalance -= amount;
    }
}
