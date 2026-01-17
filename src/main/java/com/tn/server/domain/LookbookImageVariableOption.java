package com.tn.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "lookbook_image_variable_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LookbookImageVariableOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lookbook_image_id", nullable = false)
    private LookbookImage lookbookImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variable_value_id", nullable = false)
    private PromptVariableValue promptVariableValue;

    @Column(name = "combination_id")
    private String combinationId; // 같은 조합을 묶어주는 그룹 ID (ERD 기반)
}