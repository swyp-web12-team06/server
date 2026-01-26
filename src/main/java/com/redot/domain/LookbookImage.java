package com.redot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "lookbook_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LookbookImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lookbook_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "is_representative", nullable = false)
    private Boolean isRepresentative = false;

    @Column(name = "is_preview", nullable = false)
    private Boolean isPreview = false;

    @OneToMany(mappedBy = "lookbookImage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LookbookImageVariableOption> variableOptions = new ArrayList<>();

    public LookbookImage(Prompt prompt, String imageUrl, Boolean isRepresentative) {
        this.prompt = prompt;
        this.imageUrl = imageUrl;
        this.isRepresentative = isRepresentative != null ? isRepresentative : false;
        this.isPreview = isPreview != null ? isPreview : false;
    }

    public void addVariableOption(PromptVariable variable, String value) {
        this.variableOptions.add(new LookbookImageVariableOption(this, variable, value));
    }

    public void setRepresentative(boolean representative) {
        this.isRepresentative = representative;
    }

    public Boolean getIsPreview() {
        return this.isPreview;
    }
}