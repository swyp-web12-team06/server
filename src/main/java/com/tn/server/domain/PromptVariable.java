package com.tn.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "prompt_variables")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromptVariable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variable_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;

    @Column(nullable = false)
    private String name;
}