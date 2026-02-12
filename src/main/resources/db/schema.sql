-- 1. AI 모델 테이블
CREATE TABLE IF NOT EXISTS ai_models (
    model_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    order_index INTEGER,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

-- 2. 보너스 크레딧 정책 테이블
CREATE TABLE IF NOT EXISTS bonus_credit_policy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    min_amount INTEGER NOT NULL UNIQUE,
    bonus_rate NUMERIC(38,2) NOT NULL,
    description VARCHAR(255) NOT NULL
);

-- 3. 카테고리 테이블
CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    order_index INTEGER,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

-- 4. 유저 테이블
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    nickname VARCHAR(15) UNIQUE,
    role ENUM('ADMIN','GUEST','SELLER','USER') NOT NULL,
    provider VARCHAR(255),
    provider_id VARCHAR(255),
    profile_image_key TEXT,
    bio VARCHAR(200),
    credit_balance INTEGER NOT NULL DEFAULT 0,
    warning_count INTEGER NOT NULL DEFAULT 0,
    is_banned BOOLEAN NOT NULL DEFAULT FALSE,
    terms_agreed BOOLEAN NOT NULL,
    terms_agreed_at TIMESTAMP(6),
    marketing_consent BOOLEAN NOT NULL,
    marketing_consented_at TIMESTAMP(6),
    seller_terms_agreed BOOLEAN NOT NULL DEFAULT FALSE,
    seller_terms_agreed_at TIMESTAMP(6),
    delete_reason VARCHAR(300),
    deleted_at TIMESTAMP(6),
    nickname_updated_at TIMESTAMP(6),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

-- 5. 리프레시 토큰 테이블
CREATE TABLE IF NOT EXISTS refresh_tokens (
    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP(6) NOT NULL
);

-- 6. 태그 테이블
CREATE TABLE IF NOT EXISTS tags (
    tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 7. 크레딧 충전 옵션 테이블
CREATE TABLE IF NOT EXISTS credit_charge_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount INTEGER NOT NULL
);

-- 8. 프롬프트 테이블
CREATE TABLE IF NOT EXISTS prompts (
    prompt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    master_prompt TEXT,
    price INTEGER NOT NULL,
    status ENUM('APPROVED','PENDING','REJECTED') NOT NULL,
    user_id BIGINT NOT NULL,
    model_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    preview_image_url TEXT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE, -- Soft Delete용 컬럼
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    CONSTRAINT fk_prompts_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_prompts_model FOREIGN KEY (model_id) REFERENCES ai_models(model_id),
    CONSTRAINT fk_prompts_category FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- 9. 프롬프트 변수 테이블
CREATE TABLE IF NOT EXISTS prompt_variables (
    prompt_variable_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prompt_id BIGINT NOT NULL,
    key_name VARCHAR(255) NOT NULL,
    description TEXT,
    order_index INTEGER,
    CONSTRAINT fk_prompt_variables_prompt FOREIGN KEY (prompt_id) REFERENCES prompts(prompt_id)
);

-- 10. 프롬프트-태그 매핑 테이블
CREATE TABLE IF NOT EXISTS prompt_tags (
    prompt_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (prompt_id, tag_id),
    CONSTRAINT fk_prompt_tags_prompt FOREIGN KEY (prompt_id) REFERENCES prompts(prompt_id),
    CONSTRAINT fk_prompt_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
);

-- 11. 구매 내역 테이블
CREATE TABLE IF NOT EXISTS purchases (
    purchase_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    prompt_id BIGINT NOT NULL,
    purchased_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_purchases_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_purchases_prompt FOREIGN KEY (prompt_id) REFERENCES prompts(prompt_id)
);

-- 12. 생성된 이미지 테이블
CREATE TABLE IF NOT EXISTS generated_images (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id BIGINT NOT NULL,
    image_url VARCHAR(1000) NOT NULL,
    image_quality VARCHAR(255),
    task_id VARCHAR(255) UNIQUE,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    status ENUM('PROCESSING','COMPLETED','FAILED') DEFAULT 'PROCESSING',
    created_at TIMESTAMP(6),
    CONSTRAINT fk_generated_images_purchase FOREIGN KEY (purchase_id) REFERENCES purchases(purchase_id)
);

-- 13. 생성된 이미지 변수 값 테이블
CREATE TABLE IF NOT EXISTS generated_image_variable_values (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     image_id BIGINT NOT NULL,
     prompt_variable_id BIGINT NOT NULL,
     variable_value VARCHAR(255) NOT NULL,
    CONSTRAINT fk_gen_img_var_image FOREIGN KEY (image_id) REFERENCES generated_images(image_id),
    CONSTRAINT fk_gen_img_var_variable FOREIGN KEY (prompt_variable_id) REFERENCES prompt_variables(prompt_variable_id)
);

-- 14. 룩북 이미지 테이블
CREATE TABLE IF NOT EXISTS lookbook_images (
    lookbook_image_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prompt_id BIGINT NOT NULL,
    image_url VARCHAR(1000) NOT NULL,
    is_representative BOOLEAN NOT NULL,
    is_preview BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_lookbook_images_prompt FOREIGN KEY (prompt_id) REFERENCES prompts(prompt_id)
);

-- 15. 룩북 이미지 변수 옵션 테이블
CREATE TABLE IF NOT EXISTS lookbook_image_variable_options (
    combination_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lookbook_image_id BIGINT NOT NULL,
    prompt_variable_id BIGINT NOT NULL,
    variable_value VARCHAR(255) NOT NULL, -- 예약어 충돌 방지 및 명확성 확보
    CONSTRAINT fk_lookbook_opt_image FOREIGN KEY (lookbook_image_id) REFERENCES lookbook_images(lookbook_image_id),
    CONSTRAINT fk_lookbook_opt_variable FOREIGN KEY (prompt_variable_id) REFERENCES prompt_variables(prompt_variable_id)
);

-- 16. 크레딧 트랜잭션 (입출금) 테이블
CREATE TABLE IF NOT EXISTS credit_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type TINYINT NOT NULL CHECK (type BETWEEN 0 AND 2),
    amount INTEGER NOT NULL,
    bonus INTEGER NOT NULL DEFAULT 0,
    reference_id BIGINT,
    created_at TIMESTAMP(6),
    CONSTRAINT fk_credit_tx_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 17. 결제 내역 (PG사 연동용) 테이블
CREATE TABLE IF NOT EXISTS payment_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    payment_uid VARCHAR(255),
    order_uid VARCHAR(255),
    amount INTEGER,
    created_at TIMESTAMP(6),
    CONSTRAINT fk_payment_history_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 모델 옵션 테이블 추가 (aspect_ratio, resolution 등)
CREATE TABLE IF NOT EXISTS model_options (
                                             option_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             model_id BIGINT NOT NULL,
                                             option_type VARCHAR(50) NOT NULL COMMENT 'aspect_ratio 또는 resolution',
    option_value VARCHAR(50) NOT NULL COMMENT '16:9, 1:1, 4K, HD 등',
    order_index INTEGER DEFAULT 0 COMMENT '표시 순서',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
    additional_cost INTEGER NOT NULL DEFAULT 0 COMMENT '추가 비용',
    CONSTRAINT fk_model_options_model FOREIGN KEY (model_id) REFERENCES ai_models(model_id)
    );

-- 인덱스 추가 (조회 성능 향상)
CREATE INDEX idx_model_options_model_id ON model_options(model_id);
CREATE INDEX idx_model_options_type_active ON model_options(option_type, is_active);