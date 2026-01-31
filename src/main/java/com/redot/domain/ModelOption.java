package com.redot.domain;

import jakarta.persistence.*;
import lombok.*;

// AI 모델별 옵션 Entity (aspect_ratio, resolution 등)
@Entity
@Getter
@Table(name = "model_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModelOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private AiModel aiModel;

    @Enumerated(EnumType.STRING) // 💡 Enum을 문자열로 DB에 저장
    @Column(name = "option_type", nullable = false)
    private ModelOptionType modelOptionType; // "aspect_ratio" or "resolution"

    @Column(name = "option_value", nullable = false)
    private String optionValue; // "16:9", "1:1", "4K", "HD" 등

    @Column(name = "order_index")
    private Integer orderIndex; // 표시 순서

    @Column(name = "is_active", nullable = false)
    private Boolean isActive; // 활성화 여부

    @Column(name = "additional_cost", nullable = false)
    private Integer additionalCost;

    @Builder
    public ModelOption(AiModel aiModel, ModelOptionType modelOptionType, String optionValue, Integer orderIndex, Boolean isActive) {
        this.aiModel = aiModel;
        this.modelOptionType = modelOptionType;
        this.optionValue = optionValue;
        this.orderIndex = (orderIndex != null) ? orderIndex : 0;
        this.additionalCost = (additionalCost != null) ? additionalCost : 0;
        this.isActive = (isActive != null) ? isActive : true;
    }
}