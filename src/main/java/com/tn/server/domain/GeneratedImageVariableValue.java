package com.tn.server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "generated_image_variable_values")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GeneratedImageVariableValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private GeneratedImage generatedImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variable_value_id", nullable = false)
    private PromptVariableValue promptVariableValue;

    @Builder
    public GeneratedImageVariableValue(GeneratedImage generatedImage, PromptVariableValue promptVariableValue) {
        this.generatedImage = generatedImage;
        this.promptVariableValue = promptVariableValue;
    }
}