package com.tn.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "prompt_variable_values")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromptVariableValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variable_value_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variable_id", nullable = false)
    private PromptVariable promptVariable;

    @Column(name = "variable_value", nullable = false)
    private String value;
}