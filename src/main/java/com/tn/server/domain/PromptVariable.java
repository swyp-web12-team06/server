package com.tn.server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "prompt_variables")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PromptVariable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_variable_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;

    @Column(name = "key_name", nullable = false)
    private String keyName; // e.g. "species"

    @Column(name = "variable_name")
    private String variableName; // e.g. "동물 종류"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_index")
    private Integer orderIndex;

    public PromptVariable(Prompt prompt, String keyName, String variableName, String description, Integer orderIndex) {
        this.prompt = prompt;
        this.keyName = keyName;
        this.variableName = variableName;
        this.description = description;
        this.orderIndex = orderIndex;
    }
}