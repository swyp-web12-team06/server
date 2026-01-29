package com.redot.service;

import com.redot.domain.AiModel;
import com.redot.domain.Category;
import com.redot.domain.user.User;
import com.redot.dto.product.LookbookImageCreateDto;
import com.redot.dto.product.ProductCreateRequest;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.PromptRepository;
import com.redot.repository.PurchaseRepository;
import com.redot.service.image.ImageManager;
import com.redot.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    UserService userService;
    @Mock
    PromptRepository promptRepository;
    @Mock
    PurchaseRepository purchaseRepository;
    @Mock
    ImageManager imageManager;
    @Mock
    CategoryService categoryService;
    @Mock
    AiModelService aiModelService;
    @Mock
    TagService tagService;

    @InjectMocks
    ProductService productService;

    @Nested
    @DisplayName("상품 생성 기능 테스트")
    class RegisterProduct {
        @Test
        @DisplayName("userId 검색 실패 시 예외 발생 테스트")
        void productRegister_UserFind_Fail() {
            // given
            Long userId = 999L;

            given(userService.findActiveUser(userId))
                    .willThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> productService.registerProduct(userId, new ProductCreateRequest()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("categoryId 검색 실패 시 예외 발생 테스트")
        void productRegister_CategoryFind_Fail() {
            // given
            Long categoryId = 200L;

            User testUser = User.builder()
                    .id(1L)
                    .build();

            given(userService.findActiveUser(1L))
                    .willReturn(testUser); // 혹은 User 객체 생성

            given(categoryService.getCategoryOrThrow(categoryId))
                    .willThrow(new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

            ProductCreateRequest request = ProductCreateRequest.builder()
                    .title("테스트 제목")
                    .description("설명")
                    .categoryId(categoryId)
                    .build();

            // when & then
            assertThatThrownBy(() -> productService.registerProduct(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
        }

        @Test
        @DisplayName("modelId 검색 실패 시 예외 발생 테스트")
        void productRegister_AiModelFind_Fail() {
            // given
            User testUser = User.builder()
                    .id(1L)
                    .build();

            Category testCategory = Category.builder()
                    .id(1L)
                    .build();

            given(userService.findActiveUser(1L))
                    .willReturn(testUser);

            given(categoryService.getCategoryOrThrow(1L))
                    .willReturn(testCategory);

            given(aiModelService.getModelOrThrow(999L))
                    .willThrow(new BusinessException(ErrorCode.AI_MODEL_NOT_FOUND));

            ProductCreateRequest request = ProductCreateRequest.builder()
                    .title("테스트 제목")
                    .description("설명")
                    .categoryId(1L)
                    .modelId(999L)
                    .build();

            // when & then
            assertThatThrownBy(() -> productService.registerProduct(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AI_MODEL_NOT_FOUND);
        }


        @ParameterizedTest(name = "가격 검증 실패: 가격 {0}원 -> 에러 {1}")
        @CsvSource({
                "730, INVALID_PRICE_UNIT",   // 단위 위반 (100원 단위 아님)
                "400, INVALID_PRICE_RANGE",  // 최소 금액 위반 (500원 미만)
                "1100, INVALID_PRICE_RANGE"  // 최대 금액 위반 (1000원 초과)
        })
        @DisplayName("가격 정책 검증 실패 시(100원 단위, 500~1000원 이내) 예외 발생 테스트")
        void productRegister_ProductPriceValid_Fail(int invalidPrice, ErrorCode expectedErrorCode) {
            // given
            User testUser = User.builder()
                    .id(1L)
                    .build();

            Category testCategory = Category.builder()
                    .id(1L)
                    .build();

            AiModel testAiModel = AiModel.builder()
                    .id(1L)
                    .build();

            given(userService.findActiveUser(1L))
                    .willReturn(testUser);
            given(categoryService.getCategoryOrThrow(1L))
                    .willReturn(testCategory);
            given(aiModelService.getModelOrThrow(1L))
                    .willReturn(testAiModel);

            ProductCreateRequest request = ProductCreateRequest.builder()
                    .title("테스트 제목")
                    .description("테스트 설명")
                    .categoryId(1L)
                    .modelId(1L)
                    .price(invalidPrice)
                    .build();

            // when & then
            assertThatThrownBy(() -> productService.registerProduct(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", expectedErrorCode);

        }

        @ParameterizedTest(name = "이미지 검증 실패: {1}")
        @MethodSource("provideInvalidImageScenarios")
        @DisplayName("이미지 정책 위반 시 예외 발생 (대표 이미지 개수, 프리뷰 설정 등)")
        void productRegister_InvalidImageScenario_Fail(List<LookbookImageCreateDto> invalidImages, ErrorCode expectedErrorCode) {
            // given
            User testUser = User.builder()
                    .id(1L)
                    .build();
            Category testCategory = Category.builder()
                    .id(1L)
                    .build();
            AiModel testAiModel = AiModel.builder()
                    .id(1L)
                    .build();

            given(userService.findActiveUser(1L))
                    .willReturn(testUser);
            given(categoryService.getCategoryOrThrow(1L))
                    .willReturn(testCategory);
            given(aiModelService.getModelOrThrow(1L))
                    .willReturn(testAiModel);

            int validPrice = 900;

            ProductCreateRequest request = ProductCreateRequest.builder()
                    .title("제목")
                    .description("설명")
                    .categoryId(1L)
                    .modelId(1L)
                    .price(validPrice)
                    .images(invalidImages)
                    .build();

            // when & then
            assertThatThrownBy(() -> productService.registerProduct(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", expectedErrorCode);


        }

        static Stream<Arguments> provideInvalidImageScenarios() {
            return Stream.of(
                    // 이미지가 없는 겨우
                    Arguments.of(null, ErrorCode.LOOKBOOK_IMAGE_REQUIRED),
                    Arguments.of(List.of(), ErrorCode.LOOKBOOK_IMAGE_REQUIRED),

                    // 대표 이미지가 0개인 경우
                    Arguments.of(List.of(createImage
                            (false, false)
                    ), ErrorCode.INVALID_REPRESENTATIVE_IMAGE_COUNT),

                    // 대표 이미지가 3개 초과인 경우
                    Arguments.of(List.of(
                            createImage(true, false),
                            createImage(true, false),
                            createImage(true, false),
                            createImage(true, true)
                    ), ErrorCode.INVALID_REPRESENTATIVE_IMAGE_COUNT),

                    // 프리뷰 이미지 1개가 아닌 경우
                    Arguments.of(List.of(
                            createImage(true, false)
                    ), ErrorCode.INVALID_PREVIEW_IMAGE_COUNT),
                    Arguments.of(List.of(
                            createImage(true, true),
                            createImage(true, true)
                    ), ErrorCode.INVALID_PREVIEW_IMAGE_COUNT),

                    // 프리뷰로 지정된 이미지는 반드시 대표 이미지여야 함
                    Arguments.of(List.of(
                            createImage(true, false),
                            createImage(false, true)
                    ), ErrorCode.PREVIEW_MUST_BE_REPRESENTATIVE)
            );
        }

        private static LookbookImageCreateDto createImage(boolean isRepresentative, boolean isPreview) {
            return LookbookImageCreateDto.builder()
                    .imageUrl("https://plchldr.co/i/600x400/667eea/ffffff")
                    .isRepresentative(isRepresentative)
                    .isPreview(isPreview)
                    .build();
        }

        @ParameterizedTest(name = "태그 검증 실패: {1}")
        @MethodSource("provideInvalidTagScenarios")
        @DisplayName("태그 정책 위반 시 오류 발생 (최소 2개, 최대 5개, 2~12자, 한글/영문/숫자만)")
        void productRegister_TagValidation_Fail(List<String> invalidTags, ErrorCode expectedErrorCode) {
            // given
            User testUser = User.builder()
                    .id(1L)
                    .build();
            Category testCategory = Category.builder()
                    .id(1L)
                    .build();
            AiModel testAiModel = AiModel.builder()
                    .id(1L)
                    .build();

            given(userService.findActiveUser(1L))
                    .willReturn(testUser);
            given(categoryService.getCategoryOrThrow(1L))
                    .willReturn(testCategory);
            given(aiModelService.getModelOrThrow(1L))
                    .willReturn(testAiModel);

            int validPrice = 900;

            // 이미지 등 다른 필수값은 통과하도록 더미 데이터 주입
            List<LookbookImageCreateDto> validImages = List.of(
                    LookbookImageCreateDto.builder().isRepresentative(true).isPreview(true).imageUrl("url").build()
            );

            ProductCreateRequest request = ProductCreateRequest.builder()
                    .title("제목")
                    .description("설명")
                    .price(validPrice)
                    .categoryId(1L)
                    .modelId(1L)
                    .images(validImages)
                    .tags(invalidTags)
                    .build();

            // when & then
            assertThatThrownBy(() -> productService.registerProduct(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", expectedErrorCode);
        }

        static Stream<Arguments> provideInvalidTagScenarios() {
            return Stream.of(
                    // 태그가 없을 때
                    Arguments.of(null, ErrorCode.INVALID_TAG_COUNT),
                    Arguments.of(List.of(), ErrorCode.INVALID_TAG_COUNT),

                    // 태그 개수가 부족할 때 (최소 2개)
                    Arguments.of(List.of(
                            "태그1"
                    ), ErrorCode.INVALID_TAG_COUNT),

                    // 태그 중복으로 개수가 모자랄 때
                    Arguments.of(List.of(
                            "Java",
                            "Java"
                    ), ErrorCode.INVALID_TAG_COUNT),
                    Arguments.of(List.of(
                            "Java",
                            "      "
                    ), ErrorCode.INVALID_TAG_COUNT),

                    // 태그 개수가 초과되었을 때 (최대 5개)
                    Arguments.of(List.of(
                            "태그1", "태그2", "태그3", "태그4", "태그5", "태그6"
                    ), ErrorCode.INVALID_TAG_COUNT),

                    // 태그 길이가 너무 짧을 때 (최소 2자)
                    Arguments.of(List.of(
                            "태그12",
                            "A"
                    ), ErrorCode.INVALID_TAG_LENGTH),

                    // 태그 길이가 너무 길 때 (최대 12자)
                    Arguments.of(List.of(
                            "태그1",
                            "이것은너무긴태그입니다"
                    ), ErrorCode.INVALID_TAG_LENGTH),

                    // 태그에 특수문자가 포함된 경우
                    Arguments.of(List.of(
                            "태그1",
                            "태그-2"
                    ), ErrorCode.INVALID_TAG_FORMAT),
                    Arguments.of(List.of(
                            "태그1",
                            "태그_2"
                    ), ErrorCode.INVALID_TAG_FORMAT),
                    Arguments.of(List.of(
                            "태그1",
                            "태그 2"
                    ), ErrorCode.INVALID_TAG_FORMAT)
            );
        }

        @Test
        @DisplayName("상품 등록 시 엔티티가 올바른 데이터(가격 변환 포함)로 생성되어 저장된다")
        void productRegister_PromptEntity_Success() {

        }
    }
}