-- 1. 카테고리 더미 데이터
-- (category_id, name, order_index, is_active, created_at, updated_at)
INSERT INTO categories (category_id, name, order_index, is_active, created_at, updated_at)
VALUES (1, '인물', 1, true, NOW(), NOW());

INSERT INTO categories (category_id, name, order_index, is_active, created_at, updated_at)
VALUES (2, '풍경', 2, true, NOW(), NOW());

INSERT INTO categories (category_id, name, order_index, is_active, created_at, updated_at)
VALUES (3, '일러스트', 3, true, NOW(), NOW());


-- 2. AI 모델 더미 데이터
-- (model_id, name, order_index, is_active, created_at, updated_at)
INSERT INTO ai_models (model_id, name, order_index, is_active, created_at, updated_at)
VALUES (1, 'Midjourney v6', 1, true, NOW(), NOW());

INSERT INTO ai_models (model_id, name, order_index, is_active, created_at, updated_at)
VALUES (2, 'DALL-E 3', 2, true, NOW(), NOW());

INSERT INTO ai_models (model_id, name, order_index, is_active, created_at, updated_at)
VALUES (3, 'Stable Diffusion', 3, true, NOW(), NOW());