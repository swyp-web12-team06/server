package com.tn.server.domain;

import com.tn.server.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name = "prompts")
@EntityListeners(AuditingEntityListener.class) // 생성일 자동 주입
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;

    // ManyToOne으로 카테고리 ID 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private AiModel aiModel;
  
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // 설명은 길 수 있음
    private String description;
  
    @Column(nullable = false)
    private Integer price;

    @Column(name = "master_prompt", columnDefinition = "TEXT")
    private String masterPrompt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PromptStatus status;

    @Column(name = "preview_image_url", columnDefinition = "TEXT")
    private String previewImageUrl;

    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LookbookImage> lookbookImages = new ArrayList<>();

    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PromptVariable> promptVariables = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "prompt_tags",
            joinColumns = @JoinColumn(name = "prompt_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Prompt(User seller, Category category, AiModel aiModel, String title,
                  String description, Integer price, String masterPrompt, String previewImageUrl,
                  PromptStatus status, Boolean isDeleted) {
        this.seller = seller;
        this.category = category;
        this.aiModel = aiModel;
        this.title = title;
        this.description = description;
        this.price = price;
        this.masterPrompt = masterPrompt;
        this.previewImageUrl = previewImageUrl;

        // 파라미터로 받은 값 사용 (null이면 기본값)
        this.status = (status != null) ? status : PromptStatus.APPROVED; // 기본 APPROVED
        this.isDeleted = (isDeleted != null) ? isDeleted : false;
    }

    public void addTags(Set<Tag> newTags) {
        this.tags.addAll(newTags);
    }

    public void updateTags(Set<Tag> newTags) {
        this.tags.clear();
        this.tags.addAll(newTags);
    }

    public void updateInfo(Category category, String title, String description,
                       Integer price, String previewImageUrl) {
        if (category != null) this.category = category;
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (price != null) this.price = price;
        if (previewImageUrl != null) this.previewImageUrl = previewImageUrl;
    }

    public void addLookbookImage(LookbookImage image) {
        this.lookbookImages.add(image);
    }

    public void addPromptVariable(PromptVariable variable) {
        this.promptVariables.add(variable);
    }
}