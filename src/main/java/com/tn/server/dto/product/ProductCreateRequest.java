package com.tn.server.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(min = 5, max = 50, message = "제목은 5자 이상 50자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "설명을 입력해주세요.")
    @Size(min = 20, message = "설명은 최소 20자 이상이어야 합니다.")
    private String description;

    @NotNull(message = "가격을 입력해주세요.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @NotNull(message = "AI 모델을 선택해주세요.")
    private Long modelId;

    @NotBlank(message = "프롬프트 내용을 입력해주세요.")
    private String masterPrompt;

    @Size(min = 3, max = 10, message = "태그는 최소 3개, 최대 10개까지 등록 가능합니다.")
    private List<String> tags;

    @Size(min = 1, max = 5, message = "프롬프트 변수는 최소 1개, 최대 5개까지 등록 가능합니다.")
    private List<PromptVariableCreateDto> promptVariables;

    @Size(min = 3, max = 10, message = "룩북 이미지는 최소 3개, 최대 10개까지 등록 가능합니다.")
    private List<LookbookImageCreateDto> images;
}