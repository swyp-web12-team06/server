-- ==========================================
-- SIMPLIFIED DATA (10-20 items per entity)
-- ==========================================

-- Categories (10개)
INSERT IGNORE INTO categories (category_id, name, order_index, is_active, created_at, updated_at)
VALUES
    (1, '인물', 1, true, NOW(), NOW()),
    (2, '풍경', 2, true, NOW(), NOW()),
    (3, '건축', 3, true, NOW(), NOW()),
    (4, '동물', 4, true, NOW(), NOW()),
    (5, '음식', 5, true, NOW(), NOW()),
    (6, '인테리어', 6, true, NOW(), NOW()),
    (7, '패션', 7, true, NOW(), NOW()),
    (8, 'SF/판타지', 8, true, NOW(), NOW()),
    (9, '추상', 9, true, NOW(), NOW()),
    (10, '그래픽', 10, true, NOW(), NOW());

-- AI Models (2개)
INSERT IGNORE INTO ai_models (model_id, name, order_index, is_active, created_at, updated_at)
VALUES
    (1, 'grok-imagine/text-to-image', 1, true, NOW(), NOW()),
    (2, 'nano-banana-pro', 2, true, NOW(), NOW());

-- Model Options
-- grok-imagine/text-to-image (model_id=1) - aspect_ratio만 지원
INSERT IGNORE INTO model_options (model_id, option_type, option_value, order_index, is_active, additional_cost) VALUES
    (1, 'ASPECT_RATIO', '2:3', 1, true, 0),
    (1, 'ASPECT_RATIO', '3:2', 2, true, 0),
    (1, 'ASPECT_RATIO', '1:1', 3, true, 0),
    (1, 'ASPECT_RATIO', '9:16', 4, true, 0),
    (1, 'ASPECT_RATIO', '16:9', 5, true, 0);


-- nano-banana-pro (model_id=2) - aspect_ratio + resolution 지원
INSERT IGNORE INTO model_options (model_id, option_type, option_value, order_index, is_active, additional_cost) VALUES
    (2, 'ASPECT_RATIO', '1:1', 1, true, 0),
    (2, 'ASPECT_RATIO', '2:3', 2, true, 0),
    (2, 'ASPECT_RATIO', '3:2', 3, true, 0),
    (2, 'ASPECT_RATIO', '3:4', 4, true, 0),
    (2, 'ASPECT_RATIO', '4:3', 5, true, 0),
    (2, 'ASPECT_RATIO', '4:5', 6, true, 0),
    (2, 'ASPECT_RATIO', '5:4', 7, true, 0),
    (2, 'ASPECT_RATIO', '9:16', 8, true, 0),
    (2, 'ASPECT_RATIO', '16:9', 9, true, 0),
    (2, 'ASPECT_RATIO', '21:9', 10, true, 0),
    (2, 'ASPECT_RATIO', 'auto', 11, true, 0),
    (2, 'RESOLUTION', '1K', 1, true, 0),
    (2, 'RESOLUTION', '2K', 2, true, 0),
    (2, 'RESOLUTION', '4K', 3, true, 0);

-- Users (15개)
INSERT IGNORE INTO users (user_id, email, nickname, role, credit_balance, is_banned, deleted_at, provider, provider_id, terms_agreed, marketing_consent, created_at, updated_at, warning_count)
VALUES
    (1, 'artist1@test.com', 'AI아티스트', 'USER', 100, false, null, 'local', 'artist-001', true, false, NOW(), NOW(), 0),
    (2, 'creator2@test.com', '크리에이터Kim', 'USER', 50, false, null, 'local', 'creator-002', true, true, NOW(), NOW(), 0),
    (3, 'designer3@test.com', '디자이너Park', 'USER', 80, false, null, 'local', 'designer-003', true, false, NOW(), NOW(), 0),
    (4, 'prompter4@test.com', 'PromptMaster', 'USER', 120, false, null, 'local', 'prompter-004', true, true, NOW(), NOW(), 0),
    (5, 'user5@test.com', '그림쟁이', 'USER', 30, false, null, 'local', 'user-005', true, false, NOW(), NOW(), 0),
    (6, 'maker6@test.com', 'ImageMaker', 'USER', 70, false, null, 'local', 'maker-006', true, false, NOW(), NOW(), 0),
    (7, 'artist7@test.com', '예술가Lee', 'USER', 90, false, null, 'local', 'artist-007', true, true, NOW(), NOW(), 0),
    (8, 'pro8@test.com', 'ProDesigner', 'USER', 150, false, null, 'local', 'pro-008', true, false, NOW(), NOW(), 0),
    (9, 'creative9@test.com', '창작자Choi', 'USER', 60, false, null, 'local', 'creative-009', true, false, NOW(), NOW(), 0),
    (10, 'pixel10@test.com', 'PixelArtist', 'USER', 40, false, null, 'local', 'pixel-010', true, true, NOW(), NOW(), 0),
    (11, 'digital11@test.com', '디지털작가', 'USER', 110, false, null, 'local', 'digital-011', true, false, NOW(), NOW(), 0),
    (12, 'cyber12@test.com', 'CyberCreator', 'USER', 85, false, null, 'local', 'cyber-012', true, true, NOW(), NOW(), 0),
    (13, 'neon13@test.com', 'NeonDreamer', 'USER', 75, false, null, 'local', 'neon-013', true, false, NOW(), NOW(), 0),
    (14, 'fantasy14@test.com', 'FantasyMaker', 'USER', 95, false, null, 'local', 'fantasy-014', true, false, NOW(), NOW(), 0),
    (15, 'buyer15@test.com', '일반사용자', 'USER', 50, false, null, 'local', 'buyer-015', true, true, NOW(), NOW(), 0);

-- Tags (15개)
INSERT IGNORE INTO tags (tag_id, name) VALUES
    (1, '사이버펑크'),
    (2, '네온'),
    (3, '자연'),
    (4, '판타지'),
    (5, '미래'),
    (6, '도시'),
    (7, '동물'),
    (8, '인물'),
    (9, '추상'),
    (10, '건축'),
    (11, '우주'),
    (12, '바다'),
    (13, '숲'),
    (14, '빈티지'),
    (15, '모던');

-- Prompts (20개, 제목에는 변수 없음, master_prompt에만 변수 포함)
INSERT IGNORE INTO prompts (prompt_id, user_id, category_id, model_id, title, description, price, master_prompt, status, preview_image_url, is_deleted, created_at, updated_at)
VALUES
    (1, 1, 1, 1, '사이버펑크 초상화', 'Cyberpunk style portrait', 10, 'A cyberpunk style portrait of [subject], neon lights, futuristic', 'APPROVED', 'https://picsum.photos/seed/lb1-preview/400/400', false, NOW(), NOW()),
    (2, 1, 3, 2, '네온 일러스트', 'Neon glowing illustration', 8, 'Glowing neon illustration of [subject], vibrant colors', 'APPROVED', 'https://picsum.photos/seed/lb2-preview/400/400', false, NOW(), NOW()),
    (3, 2, 2, 1, '미래 도시', 'Futuristic cityscape', 7, 'Futuristic city at [location], flying cars, skyscrapers', 'APPROVED', 'https://picsum.photos/seed/lb3-preview/400/400', false, NOW(), NOW()),
    (4, 2, 1, 2, '인물 초상화', 'Emotional portrait', 5, 'Portrait with [mood] emotion, realistic, detailed', 'APPROVED', 'https://picsum.photos/seed/lb4-preview/400/400', false, NOW(), NOW()),
    (5, 3, 3, 1, '귀여운 동물 일러스트', 'Cute animal illustration', 5, 'Cute and adorable [animal], cartoon style, colorful', 'APPROVED', 'https://picsum.photos/seed/lb5-preview/400/400', false, NOW(), NOW()),
    (6, 3, 2, 2, '자연 풍경', 'Seasonal landscape', 7, 'Beautiful natural landscape in [season], peaceful', 'APPROVED', 'https://picsum.photos/seed/lb6-preview/400/400', false, NOW(), NOW()),
    (7, 4, 4, 1, '추상 예술', 'Abstract art', 10, 'Abstract art with [color] color theme, modern', 'APPROVED', 'https://picsum.photos/seed/lb7-preview/400/400', false, NOW(), NOW()),
    (8, 4, 5, 2, '건축물', 'Architectural style', 10, '[style] architecture building, detailed, professional', 'APPROVED', 'https://picsum.photos/seed/lb8-preview/400/400', false, NOW(), NOW()),
    (9, 5, 1, 1, '판타지 전사', 'Fantasy warrior', 10, 'Fantasy warrior [character], epic, detailed armor', 'APPROVED', 'https://picsum.photos/seed/lb9-preview/400/400', false, NOW(), NOW()),
    (10, 5, 3, 2, '디지털 아트', 'Digital art object', 9, 'Digital art featuring [object], modern, creative', 'APPROVED', 'https://picsum.photos/seed/lb10-preview/400/400', false, NOW(), NOW()),
    (11, 6, 2, 1, '도시 풍경', 'City scene', 5, 'City landscape at [time], atmospheric, realistic', 'APPROVED', 'https://picsum.photos/seed/lb11-preview/400/400', false, NOW(), NOW()),
    (12, 6, 1, 2, '인물 초상', 'Age-specific portrait', 5, 'Portrait of [age] person, realistic, expressive', 'APPROVED', 'https://picsum.photos/seed/lb12-preview/400/400', false, NOW(), NOW()),
    (13, 7, 4, 1, '추상화', 'Themed abstract', 10, 'Abstract painting with [theme] theme, artistic', 'APPROVED', 'https://picsum.photos/seed/lb13-preview/400/400', false, NOW(), NOW()),
    (14, 7, 2, 2, '자연 요소', 'Nature element', 7, 'Natural scene featuring [element], beautiful, serene', 'APPROVED', 'https://picsum.photos/seed/lb14-preview/400/400', false, NOW(), NOW()),
    (15, 8, 3, 1, '신화 생물', 'Mythical creature', 8, 'Mythical [creature], fantasy style, detailed', 'APPROVED', 'https://picsum.photos/seed/lb15-preview/400/400', false, NOW(), NOW()),
    (16, 8, 5, 2, '시대 건축', 'Historical architecture', 8, 'Architecture from [period] period, authentic, detailed', 'APPROVED', 'https://picsum.photos/seed/lb16-preview/400/400', false, NOW(), NOW()),
    (17, 9, 1, 1, '직업 초상', 'Professional portrait', 7, 'Portrait of [profession], realistic, professional', 'APPROVED', 'https://picsum.photos/seed/lb17-preview/400/400', false, NOW(), NOW()),
    (18, 9, 2, 2, '날씨 풍경', 'Weather landscape', 9, 'Landscape in [weather] weather, atmospheric', 'APPROVED', 'https://picsum.photos/seed/lb18-preview/400/400', false, NOW(), NOW()),
    (19, 10, 3, 1, '음식 일러스트', 'Food illustration', 8, 'Delicious [food] illustration, appetizing, detailed', 'APPROVED', 'https://picsum.photos/seed/lb19-preview/400/400', false, NOW(), NOW()),
    (20, 10, 4, 2, '기하학 패턴', 'Geometric pattern', 7, 'Geometric pattern with [pattern] style, modern', 'APPROVED', 'https://picsum.photos/seed/lb20-preview/400/400', false, NOW(), NOW());

-- Prompt Variables (각 프롬프트당 1-2개)
INSERT IGNORE INTO prompt_variables (prompt_variable_id, prompt_id, key_name, description, order_index) VALUES
    -- Prompt 1
    (1, 1, 'subject', '초상화의 주인공 (예: 전사, 마법사, 해커)', 1),
    -- Prompt 2
    (2, 2, 'subject', '일러스트의 주제 (예: 고양이, 강아지, 로봇)', 1),
    -- Prompt 3
    (3, 3, 'location', '도시의 위치 (예: 하늘, 바다 위, 사막)', 1),
    -- Prompt 4
    (4, 4, 'mood', '감정 표현 (예: 행복한, 슬픈, 신비로운)', 1),
    -- Prompt 5
    (5, 5, 'animal', '동물 종류 (예: 강아지, 고양이, 토끼)', 1),
    -- Prompt 6
    (6, 6, 'season', '계절 (예: 봄, 여름, 가을, 겨울)', 1),
    -- Prompt 7
    (7, 7, 'color', '색상 테마 (예: 파란색, 빨간색, 무지개)', 1),
    -- Prompt 8
    (8, 8, 'style', '건축 양식 (예: 모던, 고딕, 한옥)', 1),
    -- Prompt 9
    (9, 9, 'character', '캐릭터 타입 (예: 엘프, 드워프, 인간)', 1),
    -- Prompt 10
    (10, 10, 'object', '오브젝트 (예: 검, 방패, 마법봉)', 1),
    -- Prompt 11
    (11, 11, 'time', '시간대 (예: 새벽, 정오, 저녁, 밤)', 1),
    -- Prompt 12
    (12, 12, 'age', '나이대 (예: 어린이, 청년, 노인)', 1),
    -- Prompt 13
    (13, 13, 'theme', '테마 (예: 우주, 자연, 기하학)', 1),
    -- Prompt 14
    (14, 14, 'element', '자연 요소 (예: 물, 불, 나무, 산)', 1),
    -- Prompt 15
    (15, 15, 'creature', '생물 종류 (예: 드래곤, 유니콘, 불사조)', 1),
    -- Prompt 16
    (16, 16, 'period', '시대 (예: 고대, 중세, 르네상스)', 1),
    -- Prompt 17
    (17, 17, 'profession', '직업 (예: 의사, 요리사, 예술가)', 1),
    -- Prompt 18
    (18, 18, 'weather', '날씨 (예: 맑음, 비, 눈, 안개)', 1),
    -- Prompt 19
    (19, 19, 'food', '음식 (예: 케이크, 과일, 빵)', 1),
    -- Prompt 20
    (20, 20, 'pattern', '패턴 스타일 (예: 체크, 스트라이프, 도트)', 1);

-- Prompt-Tag Mappings (각 프롬프트당 2-3개 태그)
INSERT IGNORE INTO prompt_tags (prompt_id, tag_id) VALUES
    (1, 1), (1, 2), (1, 8),
    (2, 2), (2, 3),
    (3, 5), (3, 6),
    (4, 8), (4, 15),
    (5, 7), (5, 3),
    (6, 3), (6, 13),
    (7, 9), (7, 15),
    (8, 10), (8, 15),
    (9, 4), (9, 8),
    (10, 15), (10, 9),
    (11, 6), (11, 15),
    (12, 8), (12, 14),
    (13, 9), (13, 4),
    (14, 3), (14, 13),
    (15, 4), (15, 7),
    (16, 10), (16, 14),
    (17, 8), (17, 15),
    (18, 3), (18, 13),
    (19, 7), (19, 3),
    (20, 9), (20, 15);

-- Lookbook Images (varying 1-10 per prompt)
-- Prompt 1
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (1, 1, 'https://picsum.photos/seed/lb1-preview/600/400', true, true),
    (2, 1, 'https://picsum.photos/seed/lb1-rep1/600/400', true, false),
    (3, 1, 'https://picsum.photos/seed/lb1-rep2/600/400', true, false),
    (4, 1, 'https://picsum.photos/seed/lb1-img1/600/400', false, false),
    (5, 1, 'https://picsum.photos/seed/lb1-img2/600/400', false, false);

-- Prompt 2
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (6, 2, 'https://picsum.photos/seed/lb2-preview/600/400', true, true),
    (7, 2, 'https://picsum.photos/seed/lb2-rep1/600/400', true, false),
    (8, 2, 'https://picsum.photos/seed/lb2-img1/600/400', true, false);

-- Prompt 3
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (9, 3, 'https://picsum.photos/seed/lb3-preview/600/400', true, true),
    (10, 3, 'https://picsum.photos/seed/lb3-rep1/600/400', true, false),
    (11, 3, 'https://picsum.photos/seed/lb3-rep2/600/400', true, false),
    (12, 3, 'https://picsum.photos/seed/lb3-img1/600/400', false, false),
    (13, 3, 'https://picsum.photos/seed/lb3-img2/600/400', false, false),
    (14, 3, 'https://picsum.photos/seed/lb3-img3/600/400', false, false),
    (15, 3, 'https://picsum.photos/seed/lb3-img4/600/400', false, false);

-- Prompt 4
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (16, 4, 'https://picsum.photos/seed/lb4-preview/600/400', true, true),
    (17, 4, 'https://picsum.photos/seed/lb4-img1/600/400', true, false);

-- Prompt 5
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (18, 5, 'https://picsum.photos/seed/lb5-preview/600/400', true, true),
    (19, 5, 'https://picsum.photos/seed/lb5-rep1/600/400', true, false),
    (20, 5, 'https://picsum.photos/seed/lb5-rep2/600/400', true, false),
    (21, 5, 'https://picsum.photos/seed/lb5-img1/600/400', false, false),
    (22, 5, 'https://picsum.photos/seed/lb5-img2/600/400', false, false),
    (23, 5, 'https://picsum.photos/seed/lb5-img3/600/400', false, false),
    (24, 5, 'https://picsum.photos/seed/lb5-img4/600/400', false, false),
    (25, 5, 'https://picsum.photos/seed/lb5-img5/600/400', false, false),
    (26, 5, 'https://picsum.photos/seed/lb5-img6/600/400', false, false),
    (27, 5, 'https://picsum.photos/seed/lb5-img7/600/400', false, false);

-- Prompt 6
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (28, 6, 'https://picsum.photos/seed/lb6-preview/600/400', true, true),
    (29, 6, 'https://picsum.photos/seed/lb6-rep1/600/400', true, false),
    (30, 6, 'https://picsum.photos/seed/lb6-img1/600/400', true, false),
    (31, 6, 'https://picsum.photos/seed/lb6-img2/600/400', false, false);

-- Prompt 7
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (32, 7, 'https://picsum.photos/seed/lb7-preview/600/400', true, true),
    (33, 7, 'https://picsum.photos/seed/lb7-rep1/600/400', true, false),
    (34, 7, 'https://picsum.photos/seed/lb7-rep2/600/400', true, false),
    (35, 7, 'https://picsum.photos/seed/lb7-img1/600/400', false, false),
    (36, 7, 'https://picsum.photos/seed/lb7-img2/600/400', false, false),
    (37, 7, 'https://picsum.photos/seed/lb7-img3/600/400', false, false);

-- Prompt 8
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (38, 8, 'https://picsum.photos/seed/lb8-preview/600/400', true, true);

-- Prompt 9
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (39, 9, 'https://picsum.photos/seed/lb9-preview/600/400', true, true),
    (40, 9, 'https://picsum.photos/seed/lb9-rep1/600/400', true, false),
    (41, 9, 'https://picsum.photos/seed/lb9-rep2/600/400', true, false),
    (42, 9, 'https://picsum.photos/seed/lb9-img1/600/400', false, false),
    (43, 9, 'https://picsum.photos/seed/lb9-img2/600/400', false, false),
    (44, 9, 'https://picsum.photos/seed/lb9-img3/600/400', false, false),
    (45, 9, 'https://picsum.photos/seed/lb9-img4/600/400', false, false),
    (46, 9, 'https://picsum.photos/seed/lb9-img5/600/400', false, false);

-- Prompt 10
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (47, 10, 'https://picsum.photos/seed/lb10-preview/600/400', true, true),
    (48, 10, 'https://picsum.photos/seed/lb10-rep1/600/400', true, false),
    (49, 10, 'https://picsum.photos/seed/lb10-img1/600/400', true, false),
    (50, 10, 'https://picsum.photos/seed/lb10-img2/600/400', false, false),
    (51, 10, 'https://picsum.photos/seed/lb10-img3/600/400', false, false);

-- Prompt 11
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (52, 11, 'https://picsum.photos/seed/lb11-preview/600/400', true, true);

-- Prompt 12
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (55, 12, 'https://picsum.photos/seed/lb12-preview/600/400', true, true),
    (56, 12, 'https://picsum.photos/seed/lb12-rep1/600/400', true, false),
    (57, 12, 'https://picsum.photos/seed/lb12-rep2/600/400', true, false),
    (58, 12, 'https://picsum.photos/seed/lb12-img1/600/400', false, false),
    (59, 12, 'https://picsum.photos/seed/lb12-img2/600/400', false, false),
    (60, 12, 'https://picsum.photos/seed/lb12-img3/600/400', false, false),
    (61, 12, 'https://picsum.photos/seed/lb12-img4/600/400', false, false),
    (62, 12, 'https://picsum.photos/seed/lb12-img5/600/400', false, false),
    (63, 12, 'https://picsum.photos/seed/lb12-img6/600/400', false, false);

-- Prompt 13
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (64, 13, 'https://picsum.photos/seed/lb13-preview/600/400', true, true),
    (65, 13, 'https://picsum.photos/seed/lb13-rep1/600/400', true, false),
    (66, 13, 'https://picsum.photos/seed/lb13-img1/600/400', false, false),
    (67, 13, 'https://picsum.photos/seed/lb13-img2/600/400', false, false),
    (68, 13, 'https://picsum.photos/seed/lb13-img3/600/400', false, false),
    (69, 13, 'https://picsum.photos/seed/lb13-img4/600/400', false, false);

-- Prompt 14
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (70, 14, 'https://picsum.photos/seed/lb14-preview/600/400', true, true),
    (71, 14, 'https://picsum.photos/seed/lb14-rep1/600/400', true, false),
    (72, 14, 'https://picsum.photos/seed/lb14-rep2/600/400', true, false),
    (73, 14, 'https://picsum.photos/seed/lb14-img1/600/400', false, false);

-- Prompt 15
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (74, 15, 'https://picsum.photos/seed/lb15-preview/600/400', true, true),
    (75, 15, 'https://picsum.photos/seed/lb15-rep1/600/400', true, false),
    (76, 15, 'https://picsum.photos/seed/lb15-rep2/600/400', true, false),
    (77, 15, 'https://picsum.photos/seed/lb15-img1/600/400', false, false),
    (78, 15, 'https://picsum.photos/seed/lb15-img2/600/400', false, false),
    (79, 15, 'https://picsum.photos/seed/lb15-img3/600/400', false, false),
    (80, 15, 'https://picsum.photos/seed/lb15-img4/600/400', false, false);

-- Prompt 16
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (81, 16, 'https://picsum.photos/seed/lb16-preview/600/400', true, true),
    (82, 16, 'https://picsum.photos/seed/lb16-rep1/600/400', true, false);

-- Prompt 17
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (83, 17, 'https://picsum.photos/seed/lb17-preview/600/400', true, true),
    (84, 17, 'https://picsum.photos/seed/lb17-rep1/600/400', true, false),
    (85, 17, 'https://picsum.photos/seed/lb17-img1/600/400', false, false),
    (86, 17, 'https://picsum.photos/seed/lb17-img2/600/400', false, false),
    (87, 17, 'https://picsum.photos/seed/lb17-img3/600/400', false, false);

-- Prompt 18
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (88, 18, 'https://picsum.photos/seed/lb18-preview/600/400', true, true),
    (89, 18, 'https://picsum.photos/seed/lb18-rep1/600/400', true, false),
    (90, 18, 'https://picsum.photos/seed/lb18-rep2/600/400', true, false),
    (91, 18, 'https://picsum.photos/seed/lb18-img1/600/400', false, false),
    (92, 18, 'https://picsum.photos/seed/lb18-img2/600/400', false, false),
    (93, 18, 'https://picsum.photos/seed/lb18-img3/600/400', false, false),
    (94, 18, 'https://picsum.photos/seed/lb18-img4/600/400', false, false),
    (95, 18, 'https://picsum.photos/seed/lb18-img5/600/400', false, false);

-- Prompt 19
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (96, 19, 'https://picsum.photos/seed/lb19-preview/600/400', true, true),
    (97, 19, 'https://picsum.photos/seed/lb19-rep1/600/400', true, false),
    (98, 19, 'https://picsum.photos/seed/lb19-img1/600/400', true, false),
    (99, 19, 'https://picsum.photos/seed/lb19-img2/600/400', false, false);

-- Prompt 20
INSERT IGNORE INTO lookbook_images (lookbook_image_id, prompt_id, image_url, is_representative, is_preview) VALUES
    (100, 20, 'https://picsum.photos/seed/lb20-preview/600/400', true, true),
    (101, 20, 'https://picsum.photos/seed/lb20-rep1/600/400', true, false),
    (102, 20, 'https://picsum.photos/seed/lb20-rep2/600/400', true, false),
    (103, 20, 'https://picsum.photos/seed/lb20-img1/600/400', false, false),
    (104, 20, 'https://picsum.photos/seed/lb20-img2/600/400', false, false),
    (105, 20, 'https://picsum.photos/seed/lb20-img3/600/400', false, false);

-- Lookbook Image Variable Options (첫 번째 룩북 이미지에 변수 값 예시 추가)
INSERT IGNORE INTO lookbook_image_variable_options (lookbook_image_id, prompt_variable_id, variable_value) VALUES
    (1, 1, 'warrior'),
    (6, 2, 'cat'),
    (9, 3, 'sky'),
    (16, 4, 'happy'),
    (18, 5, 'puppy'),
    (28, 6, 'spring'),
    (32, 7, 'blue'),
    (38, 8, 'modern'),
    (39, 9, 'elf'),
    (47, 10, 'sword'),
    (52, 11, 'sunset'),
    (55, 12, 'young adult'),
    (64, 13, 'space'),
    (70, 14, 'water'),
    (74, 15, 'dragon'),
    (81, 16, 'medieval'),
    (83, 17, 'chef'),
    (88, 18, 'rainy'),
    (96, 19, 'cake'),
    (100, 20, 'stripe');

-- Purchases (10개)
INSERT IGNORE INTO purchases (purchase_id, user_id, prompt_id, purchased_at) VALUES
    (1, 11, 1, NOW()),
    (2, 11, 2, NOW()),
    (3, 12, 3, NOW()),
    (4, 12, 4, NOW()),
    (5, 13, 5, NOW()),
    (6, 13, 6, NOW()),
    (7, 14, 7, NOW()),
    (8, 14, 8, NOW()),
    (9, 15, 9, NOW()),
    (10, 15, 10, NOW());

-- Credit Charge Options (5개)
INSERT IGNORE INTO credit_charge_option (id, amount) VALUES
    (1, 3000),
    (2, 5000),
    (3, 10000),
    (4, 30000),
    (5, 50000);

-- Bonus Credit Policies (3개)
INSERT IGNORE INTO bonus_credit_policy (id, min_amount, bonus_rate, description) VALUES
    (1, 5000, 0.05, '5% Bonus'),
    (2, 30000, 0.10, '10% Bonus'),
    (3, 50000, 0.15, '15% Bonus');

INSERT IGNORE INTO purchases (user_id, prompt_id, purchased_at) VALUES
    (1, 1, CURRENT_TIMESTAMP);