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
    @Column(name = "combination_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lookbook_image_id", nullable = false)
    private LookbookImage lookbookImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_variable_id", nullable = false)
    private PromptVariable promptVariable;

    @Column(name = "variable_value", nullable = false)
    private String variableValue;

    public LookbookImageVariableOption(LookbookImage lookbookImage, PromptVariable promptVariable, String variableValue) {
        this.lookbookImage = lookbookImage;
        this.promptVariable = promptVariable;
        this.variableValue = variableValue;
    }
}