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
    @JoinColumn(name = "prompt_variable_id", nullable = false)
    private PromptVariable promptVariable;

    @Column(name = "variable_value", nullable = false)
    private String value;

    @Builder
    public GeneratedImageVariableValue(GeneratedImage generatedImage, PromptVariable promptVariable, String value) {
        this.generatedImage = generatedImage;
        this.promptVariable = promptVariable;
        this.value = value;
    }
}