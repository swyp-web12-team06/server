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
    (2, 'RESOLUTION', '4K', 3, true, 3);

-- Users (15개)
INSERT IGNORE INTO users (user_id, email, nickname, role, credit_balance, is_banned, deleted_at, provider, provider_id, terms_agreed, marketing_consent, seller_terms_agreed, created_at, updated_at, warning_count)
VALUES
    (1, 'artist1@test.com', 'AI아티스트', 'USER', 100, false, null, 'local', 'artist-001', true, false, false, NOW(), NOW(), 0),
    (2, 'creator2@test.com', '크리에이터Kim', 'SELLER', 50, false, null, 'local', 'creator-002', true, true, true, NOW(), NOW(), 0),
    (3, 'designer3@test.com', '디자이너Park', 'USER', 80, false, null, 'local', 'designer-003', true, false, false, NOW(), NOW(), 0),
    (4, 'prompter4@test.com', 'PromptMaster', 'USER', 120, false, null, 'local', 'prompter-004', true, true, false, NOW(), NOW(), 0),
    (5, 'user5@test.com', '그림쟁이', 'USER', 30, false, null, 'local', 'user-005', true, false, false, NOW(), NOW(), 0),
    (6, 'maker6@test.com', 'ImageMaker', 'USER', 70, false, null, 'local', 'maker-006', true, false, false, NOW(), NOW(), 0),
    (7, 'artist7@test.com', '예술가Lee', 'USER', 90, false, null, 'local', 'artist-007', true, true, false, NOW(), NOW(), 0),
    (8, 'pro8@test.com', 'ProDesigner', 'USER', 150, false, null, 'local', 'pro-008', true, false, false, NOW(), NOW(), 0),
    (9, 'creative9@test.com', '창작자Choi', 'USER', 60, false, null, 'local', 'creative-009', true, false, false, NOW(), NOW(), 0),
    (10, 'pixel10@test.com', 'PixelArtist', 'USER', 40, false, null, 'local', 'pixel-010', true, true, false, NOW(), NOW(), 0),
    (11, 'digital11@test.com', '디지털작가', 'USER', 110, false, null, 'local', 'digital-011', true, false, false, NOW(), NOW(), 0),
    (12, 'cyber12@test.com', 'CyberCreator', 'USER', 85, false, null, 'local', 'cyber-012', true, true, false, NOW(), NOW(), 0),
    (13, 'neon13@test.com', 'NeonDreamer', 'USER', 75, false, null, 'local', 'neon-013', true, false, false, NOW(), NOW(), 0),
    (14, 'fantasy14@test.com', 'FantasyMaker', 'USER', 95, false, null, 'local', 'fantasy-014', true, false, false, NOW(), NOW(), 0),
    (15, 'buyer15@test.com', '일반사용자', 'USER', 50, false, null, 'local', 'buyer-015', true, true, false, NOW(), NOW(), 0);

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

-- 1. Prompts (20개 재정의 - 변수 개수 패턴: 1,2,3,4,5 순환)
INSERT IGNORE INTO prompts (prompt_id, user_id, category_id, model_id, title, description, price, master_prompt, status, preview_image_url, is_deleted, created_at, updated_at) VALUES
-- 패턴 1: 변수 1개
(1, 1, 1, 1, '사이버펑크 초상화', 'Cyberpunk style portrait', 10, 'A cyberpunk style portrait of [subject], neon lights, futuristic', 'APPROVED', 'https://picsum.photos/seed/lb1-preview/400/400', false, NOW(), NOW()),
-- 패턴 2: 변수 2개
(2, 1, 3, 2, '네온 일러스트', 'Neon glowing illustration', 8, 'Glowing neon illustration of [subject] in [color_tone] colors, vibrant', 'APPROVED', 'https://picsum.photos/seed/lb2-preview/400/400', false, NOW(), NOW()),
-- 패턴 3: 변수 3개
(3, 2, 2, 1, '미래 도시', 'Futuristic cityscape', 7, 'Futuristic city at [location] during [time_of_day] with [weather] weather, flying cars', 'APPROVED', 'https://picsum.photos/seed/lb3-preview/400/400', false, NOW(), NOW()),
-- 패턴 4: 변수 4개
(4, 2, 1, 2, '인물 초상화', 'Emotional portrait', 5, 'Portrait with [mood] emotion, [lighting] lighting, [gaze] gaze, against [background] background', 'APPROVED', 'https://picsum.photos/seed/lb4-preview/400/400', false, NOW(), NOW()),
-- 패턴 5: 변수 5개
(5, 3, 3, 1, '귀여운 동물 일러스트', 'Cute animal illustration', 5, 'Cute [animal] wearing [accessory], doing [action], [background_color] background, in [art_style] style', 'APPROVED', 'https://picsum.photos/seed/lb5-preview/400/400', false, NOW(), NOW()),

-- 패턴 1: 변수 1개
(6, 3, 2, 2, '자연 풍경', 'Seasonal landscape', 7, 'Beautiful natural landscape in [season], peaceful', 'APPROVED', 'https://picsum.photos/seed/lb6-preview/400/400', false, NOW(), NOW()),
-- 패턴 2: 변수 2개
(7, 4, 4, 1, '추상 예술', 'Abstract art', 10, 'Abstract art with [color] color theme and [shape] shapes, modern', 'APPROVED', 'https://picsum.photos/seed/lb7-preview/400/400', false, NOW(), NOW()),
-- 패턴 3: 변수 3개
(8, 4, 5, 2, '건축물', 'Architectural style', 10, '[style] architecture building made of [material] in [environment], detailed', 'APPROVED', 'https://picsum.photos/seed/lb8-preview/400/400', false, NOW(), NOW()),
-- 패턴 4: 변수 4개
(9, 5, 1, 1, '판타지 전사', 'Fantasy warrior', 10, 'Fantasy warrior [character] wielding [weapon] wearing [armor], performing [action]', 'APPROVED', 'https://picsum.photos/seed/lb9-preview/400/400', false, NOW(), NOW()),
-- 패턴 5: 변수 5개
(10, 5, 3, 2, '디지털 아트', 'Digital art object', 9, 'Digital art featuring [object] made of [material_finish], [lighting_setup] lighting, [background_gradient] gradient, rendered in [render_engine]', 'APPROVED', 'https://picsum.photos/seed/lb10-preview/400/400', false, NOW(), NOW()),

-- 패턴 1: 변수 1개
(11, 6, 2, 1, '도시 풍경', 'City scene', 5, 'City landscape at [time], atmospheric, realistic', 'APPROVED', 'https://picsum.photos/seed/lb11-preview/400/400', false, NOW(), NOW()),
-- 패턴 2: 변수 2개
(12, 6, 1, 2, '인물 초상', 'Age-specific portrait', 5, 'Portrait of [age] person of [gender] gender, realistic, expressive', 'APPROVED', 'https://picsum.photos/seed/lb12-preview/400/400', false, NOW(), NOW()),
-- 패턴 3: 변수 3개
(13, 7, 4, 1, '추상화', 'Themed abstract', 10, 'Abstract painting with [theme] theme, [texture] texture, [emotion] feeling', 'APPROVED', 'https://picsum.photos/seed/lb13-preview/400/400', false, NOW(), NOW()),
-- 패턴 4: 변수 4개
(14, 7, 2, 2, '자연 요소', 'Nature element', 7, 'Natural scene featuring [element] from [perspective] view, [lighting_mood] lighting, surrounded by [flora]', 'APPROVED', 'https://picsum.photos/seed/lb14-preview/400/400', false, NOW(), NOW()),
-- 패턴 5: 변수 5개
(15, 8, 3, 1, '신화 생물', 'Mythical creature', 8, 'Mythical [creature] with [element_power] power, [wing_style] wings, [eye_color] eyes, living in [habitat]', 'APPROVED', 'https://picsum.photos/seed/lb15-preview/400/400', false, NOW(), NOW()),

-- 패턴 1: 변수 1개
(16, 8, 5, 2, '시대 건축', 'Historical architecture', 8, 'Architecture from [period] period, authentic, detailed', 'APPROVED', 'https://picsum.photos/seed/lb16-preview/400/400', false, NOW(), NOW()),
-- 패턴 2: 변수 2개
(17, 9, 1, 1, '직업 초상', 'Professional portrait', 7, 'Portrait of [profession] wearing [clothing], realistic, professional', 'APPROVED', 'https://picsum.photos/seed/lb17-preview/400/400', false, NOW(), NOW()),
-- 패턴 3: 변수 3개
(18, 9, 2, 2, '날씨 풍경', 'Weather landscape', 9, 'Landscape in [weather] weather showing [terrain] terrain with [vegetation], atmospheric', 'APPROVED', 'https://picsum.photos/seed/lb18-preview/400/400', false, NOW(), NOW()),
-- 패턴 4: 변수 4개
(19, 10, 3, 1, '음식 일러스트', 'Food illustration', 8, 'Delicious [food] on [plate_style], garnished with [garnish], served with [drink]', 'APPROVED', 'https://picsum.photos/seed/lb19-preview/400/400', false, NOW(), NOW()),
-- 패턴 5: 변수 5개
(20, 10, 4, 2, '기하학 패턴', 'Geometric pattern', 7, 'Geometric pattern with [pattern] style, [primary_color] and [secondary_color], [line_thickness] lines, [complexity] complexity', 'APPROVED', 'https://picsum.photos/seed/lb20-preview/400/400', false, NOW(), NOW());

-- 2. Prompt Variables (60개 변수 데이터)
INSERT IGNORE INTO prompt_variables (prompt_variable_id, prompt_id, key_name, description, order_index) VALUES
    -- P1 (1개)
    (1, 1, 'subject', '주인공 (예: 전사, 해커)', 1),
    -- P2 (2개)
    (2, 2, 'subject', '주제 (예: 고양이, 로봇)', 1),
    (3, 2, 'color_tone', '색조 (예: 핑크, 블루)', 2),
    -- P3 (3개)
    (4, 3, 'location', '위치 (예: 하늘 위, 바다)', 1),
    (5, 3, 'time_of_day', '시간대 (예: 석양, 자정)', 2),
    (6, 3, 'weather', '날씨 (예: 비 오는, 맑은)', 3),
    -- P4 (4개)
    (7, 4, 'mood', '감정 (예: 슬픈, 기쁜)', 1),
    (8, 4, 'lighting', '조명 (예: 렘브란트, 네온)', 2),
    (9, 4, 'gaze', '시선 (예: 정면, 먼 곳)', 3),
    (10, 4, 'background', '배경 (예: 단색, 도시)', 4),
    -- P5 (5개)
    (11, 5, 'animal', '동물 (예: 강아지, 토끼)', 1),
    (12, 5, 'accessory', '액세서리 (예: 안경, 모자)', 2),
    (13, 5, 'action', '행동 (예: 자는, 점프하는)', 3),
    (14, 5, 'background_color', '배경색 (예: 파스텔 톤)', 4),
    (15, 5, 'art_style', '화풍 (예: 수채화, 3D)', 5),

    -- P6 (1개)
    (16, 6, 'season', '계절 (예: 가을, 겨울)', 1),
    -- P7 (2개)
    (17, 7, 'color', '색상 (예: 빨강, 검정)', 1),
    (18, 7, 'shape', '도형 (예: 원형, 삼각형)', 2),
    -- P8 (3개)
    (19, 8, 'style', '양식 (예: 모던, 고딕)', 1),
    (20, 8, 'material', '재질 (예: 유리, 콘크리트)', 2),
    (21, 8, 'environment', '환경 (예: 숲 속, 도심)', 3),
    -- P9 (4개)
    (22, 9, 'character', '캐릭터 (예: 엘프, 오크)', 1),
    (23, 9, 'weapon', '무기 (예: 검, 도끼)', 2),
    (24, 9, 'armor', '갑옷 (예: 판금, 가죽)', 3),
    (25, 9, 'action', '동작 (예: 공격하는, 방어하는)', 4),
    -- P10 (5개)
    (26, 10, 'object', '오브젝트 (예: 구, 큐브)', 1),
    (27, 10, 'material_finish', '마감 (예: 금속, 플라스틱)', 2),
    (28, 10, 'lighting_setup', '조명 세팅 (예: 스튜디오, 자연광)', 3),
    (29, 10, 'background_gradient', '배경 그라데이션 (예: 오렌지-퍼플)', 4),
    (30, 10, 'render_engine', '렌더링 느낌 (예: 언리얼, 옥테인)', 5),

    -- P11 (1개)
    (31, 11, 'time', '시간 (예: 새벽, 황혼)', 1),
    -- P12 (2개)
    (32, 12, 'age', '나이 (예: 노인, 아이)', 1),
    (33, 12, 'gender', '성별 (예: 남성, 여성)', 2),
    -- P13 (3개)
    (34, 13, 'theme', '테마 (예: 우주, 심해)', 1),
    (35, 13, 'texture', '질감 (예: 거친, 부드러운)', 2),
    (36, 13, 'emotion', '느낌 (예: 혼란, 평온)', 3),
    -- P14 (4개)
    (37, 14, 'element', '요소 (예: 불, 물)', 1),
    (38, 14, 'perspective', '구도 (예: 탑뷰, 로우앵글)', 2),
    (39, 14, 'lighting_mood', '조명 분위기 (예: 신비로운, 밝은)', 3),
    (40, 14, 'flora', '식물 (예: 덩굴, 꽃)', 4),
    -- P15 (5개)
    (41, 15, 'creature', '생물 (예: 드래곤, 피닉스)', 1),
    (42, 15, 'element_power', '속성 (예: 화염, 냉기)', 2),
    (43, 15, 'wing_style', '날개 (예: 박쥐 날개, 깃털)', 3),
    (44, 15, 'eye_color', '눈 색 (예: 붉은, 금색)', 4),
    (45, 15, 'habitat', '서식지 (예: 화산, 빙하)', 5),

    -- P16 (1개)
    (46, 16, 'period', '시대 (예: 중세, 미래)', 1),
    -- P17 (2개)
    (47, 17, 'profession', '직업 (예: 의사, 경찰)', 1),
    (48, 17, 'clothing', '의상 (예: 가운, 제복)', 2),
    -- P18 (3개)
    (49, 18, 'weather', '날씨 (예: 폭풍, 안개)', 1),
    (50, 18, 'terrain', '지형 (예: 산, 평원)', 2),
    (51, 18, 'vegetation', '식생 (예: 소나무, 잔디)', 3),
    -- P19 (4개)
    (52, 19, 'food', '음식 (예: 파스타, 스테이크)', 1),
    (53, 19, 'plate_style', '접시 (예: 흰색 원형, 나무 도마)', 2),
    (54, 19, 'garnish', '가니쉬 (예: 파슬리, 치즈)', 3),
    (55, 19, 'drink', '음료 (예: 와인, 콜라)', 4),
    -- P20 (5개)
    (56, 20, 'pattern', '패턴 (예: 체크, 줄무늬)', 1),
    (57, 20, 'primary_color', '주색상 (예: 검정, 흰색)', 2),
    (58, 20, 'secondary_color', '보조색 (예: 노랑, 민트)', 3),
    (59, 20, 'line_thickness', '선 굵기 (예: 얇은, 굵은)', 4),
    (60, 20, 'complexity', '복잡도 (예: 단순한, 복잡한)', 5);

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

-- Lookbook Image Variable Options (Prompt 1~5 Full Data)
INSERT IGNORE INTO lookbook_image_variable_options (lookbook_image_id, prompt_variable_id, variable_value) VALUES
    -- [Prompt 1] 사이버펑크 초상화 (변수: subject / ID: 1)
    -- 이미지: 1~5번
    (1, 1, 'cyborg warrior'),      -- Image 1 (Preview)
    (2, 1, 'neon hacker'),         -- Image 2
    (3, 1, 'android nurse'),       -- Image 3
    (4, 1, 'street samurai'),      -- Image 4
    (5, 1, 'data broker'),         -- Image 5

    -- [Prompt 2] 네온 일러스트 (변수: subject, color_tone / ID: 2, 3)
    -- 이미지: 6~8번
    -- Image 6 (Preview)
    (6, 2, 'cyber cat'),
    (6, 3, 'neon pink'),
    -- Image 7
    (7, 2, 'retro robot'),
    (7, 3, 'electric blue'),
    -- Image 8
    (8, 2, 'synthwave car'),
    (8, 3, 'vibrant purple'),

    -- [Prompt 3] 미래 도시 (변수: location, time, weather / ID: 4, 5, 6)
    -- 이미지: 9~15번
    (9, 4, 'sky platform'), (9, 5, 'sunset'), (9, 6, 'clear sky'),
    -- Image 10
    (10, 4, 'underwater dome'), (10, 5, 'noon'), (10, 6, 'sunny'),
    -- Image 11
    (11, 4, 'mars colony'), (11, 5, 'night'), (11, 6, 'dusty storm'),
    -- Image 12
    (12, 4, 'floating island'), (12, 5, 'dawn'), (12, 6, 'foggy'),
    -- Image 13
    (13, 4, 'desert oasis'), (13, 5, 'midnight'), (13, 6, 'starry'),
    -- Image 14
    (14, 4, 'arctic base'), (14, 5, 'aurora time'), (14, 6, 'snowy'),
    -- Image 15
    (15, 4, 'jungle canopy'), (15, 5, 'afternoon'), (15, 6, 'rainy'),

    -- [Prompt 4] 인물 초상화 (변수: mood, lighting, gaze, background / ID: 7, 8, 9, 10)
    -- 이미지: 16~17번
    -- Image 16 (Preview)
    (16, 7, 'melancholic'), (16, 8, 'rembrandt'), (16, 9, 'looking away'), (16, 10, 'rainy window'),
    -- Image 17
    (17, 7, 'joyful'), (17, 8, 'high-key'), (17, 9, 'direct eye contact'), (17, 10, 'flower field'),

    -- [Prompt 5] 귀여운 동물 (변수: animal, accessory, action, bg_color, style / ID: 11~15)
    -- 이미지: 18~27번
    -- Image 18 (Preview)
    (18, 11, 'golden retriever'), (18, 12, 'red scarf'), (18, 13, 'running'), (18, 14, 'pastel blue'), (18, 15, 'watercolor'),
    -- Image 19
    (19, 11, 'tabby cat'), (19, 12, 'wizard hat'), (19, 13, 'sleeping'), (19, 14, 'soft pink'), (19, 15, 'oil painting'),
    -- Image 20
    (20, 11, 'white bunny'), (20, 12, 'bow tie'), (20, 13, 'eating carrot'), (20, 14, 'mint green'), (20, 15, 'colored pencil'),
    -- Image 21
    (21, 11, 'hamster'), (21, 12, 'tiny glasses'), (21, 13, 'reading'), (21, 14, 'lemon yellow'), (21, 15, '3D render'),
    -- Image 22
    (22, 11, 'panda'), (22, 12, 'bamboo hat'), (22, 13, 'sitting'), (22, 14, 'white'), (22, 15, 'ink wash'),
    -- Image 23
    (23, 11, 'penguin'), (23, 12, 'pilot goggles'), (23, 13, 'sliding'), (23, 14, 'icy blue'), (23, 15, 'vector art'),
    -- Image 24
    (24, 11, 'red fox'), (24, 12, 'flower crown'), (24, 13, 'smiling'), (24, 14, 'forest green'), (24, 15, 'digital painting'),
    -- Image 25
    (25, 11, 'koala'), (25, 12, 'backpack'), (25, 13, 'climbing'), (25, 14, 'sky blue'), (25, 15, 'cartoon'),
    -- Image 26
    (26, 11, 'hedgehog'), (26, 12, 'boots'), (26, 13, 'walking'), (26, 14, 'beige'), (26, 15, 'sketch'),
    -- Image 27
    (27, 11, 'shiba inu'), (27, 12, 'bandana'), (27, 13, 'barking'), (27, 14, 'orange'), (27, 15, 'pop art');

    -- Lookbook Image Variable Options (Prompt 6~10 Full Data)
    INSERT IGNORE INTO lookbook_image_variable_options (lookbook_image_id, prompt_variable_id, variable_value) VALUES

    -- [Prompt 6] 자연 풍경 (변수: season / ID: 16)
    -- 이미지: 28~31번
    (28, 16, 'winter'),        -- Image 28 (Preview)
    (29, 16, 'spring'),        -- Image 29
    (30, 16, 'summer'),        -- Image 30
    (31, 16, 'autumn'),        -- Image 31

    -- [Prompt 7] 추상 예술 (변수: color, shape / ID: 17, 18)
    -- 이미지: 32~37번
    -- Image 32 (Preview)
    (32, 17, 'vivid orange'), (32, 18, 'triangles'),
    -- Image 33
    (33, 17, 'deep blue'), (33, 18, 'circles'),
    -- Image 34
    (34, 17, 'neon green'), (34, 18, 'squares'),
    -- Image 35
    (35, 17, 'red and black'), (35, 18, 'chaotic lines'),
    -- Image 36
    (36, 17, 'pastel pink'), (36, 18, 'organic blobs'),
    -- Image 37
    (37, 17, 'metallic gold'), (37, 18, 'hexagons'),

    -- [Prompt 8] 건축물 (변수: style, material, environment / ID: 19, 20, 21)
    -- 이미지: 38번 (1개뿐)
    -- Image 38 (Preview)
    (38, 19, 'brutalist'), (38, 20, 'raw concrete'), (38, 21, 'cloudy sky'),

    -- [Prompt 9] 판타지 전사 (변수: character, weapon, armor, action / ID: 22, 23, 24, 25)
    -- 이미지: 39~46번
    -- Image 39 (Preview)
    (39, 22, 'female knight'), (39, 23, 'greatsword'), (39, 24, 'silver plate'), (39, 25, 'battle stance'),
    -- Image 40
    (40, 22, 'orc berserker'), (40, 23, 'giant axe'), (40, 24, 'fur and leather'), (40, 25, 'roaring'),
    -- Image 41
    (41, 22, 'elf archer'), (41, 23, 'longbow'), (41, 24, 'light chainmail'), (41, 25, 'aiming'),
    -- Image 42
    (42, 22, 'dwarf paladin'), (42, 23, 'warhammer'), (42, 24, 'gold heavy armor'), (42, 25, 'guarding'),
    -- Image 43
    (43, 22, 'dark elf rogue'), (43, 23, 'dual daggers'), (43, 24, 'black leather'), (43, 25, 'sneaking'),
    -- Image 44
    (44, 22, 'human samurai'), (44, 23, 'katana'), (44, 24, 'red lacquer armor'), (44, 25, 'sheathing sword'),
    -- Image 45
    (45, 22, 'undead skeleton'), (45, 23, 'rusty spear'), (45, 24, 'broken shield'), (45, 25, 'marching'),
    -- Image 46
    (46, 22, 'viking raider'), (46, 23, 'battleaxe'), (46, 24, 'chainmail'), (46, 25, 'charging'),

    -- [Prompt 10] 디지털 아트 (변수: object, material_f, lighting, bg_grad, render / ID: 26~30)
    -- 이미지: 47~51번
    -- Image 47 (Preview)
    (47, 26, 'floating cube'), (47, 27, 'glossy metal'), (47, 28, 'studio softbox'), (47, 29, 'blue to purple'), (47, 30, 'octane render'),
    -- Image 48
    (48, 26, 'melting sphere'), (48, 27, 'liquid chrome'), (48, 28, 'neon rim'), (48, 29, 'red to black'), (48, 30, 'unreal engine 5'),
    -- Image 49
    (49, 26, 'twisted torus'), (49, 27, 'frosted glass'), (49, 28, 'natural sunlight'), (49, 29, 'green to yellow'), (49, 30, 'redshift'),
    -- Image 50
    (50, 26, 'abstract knot'), (50, 27, 'matte plastic'), (50, 28, 'cinematic'), (50, 29, 'pink to cyan'), (50, 30, 'blender cycles'),
    -- Image 51
    (51, 26, 'low-poly fox'), (51, 27, 'paper texture'), (51, 28, 'flat lighting'), (51, 29, 'white to grey'), (51, 30, 'v-ray');

-- Lookbook Image Variable Options (Prompt 11~15 Full Data)
INSERT IGNORE INTO lookbook_image_variable_options (lookbook_image_id, prompt_variable_id, variable_value) VALUES
    -- [Prompt 11] 도시 풍경 (변수: time / ID: 31)
    -- 이미지: 52번 (1개)
    (52, 31, 'midnight'),

    -- [Prompt 12] 인물 초상 (변수: age, gender / ID: 32, 33)
    -- 이미지: 55~63번
    -- Image 55 (Preview)
    (55, 32, 'elderly'), (55, 33, 'male'),
    -- Image 56
    (56, 32, 'young adult'), (56, 33, 'female'),
    -- Image 57
    (57, 32, 'child'), (57, 33, 'male'),
    -- Image 58
    (58, 32, 'middle-aged'), (58, 33, 'female'),
    -- Image 59
    (59, 32, 'teenager'), (59, 33, 'male'),
    -- Image 60
    (60, 32, 'elderly'), (60, 33, 'female'),
    -- Image 61
    (61, 32, 'young adult'), (61, 33, 'male'),
    -- Image 62
    (62, 32, 'child'), (62, 33, 'female'),
    -- Image 63
    (63, 32, 'middle-aged'), (63, 33, 'male'),

    -- [Prompt 13] 추상화 (변수: theme, texture, emotion / ID: 34, 35, 36)
    -- 이미지: 64~69번
    -- Image 64 (Preview)
    (64, 34, 'ocean depths'), (64, 35, 'fluid'), (64, 36, 'mysterious'),
    -- Image 65
    (65, 34, 'cosmic void'), (65, 35, 'stardust'), (65, 36, 'lonely'),
    -- Image 66
    (66, 34, 'forest fire'), (66, 35, 'crackled'), (66, 36, 'chaotic'),
    -- Image 67
    (67, 34, 'spring garden'), (67, 35, 'soft petals'), (67, 36, 'joyful'),
    -- Image 68
    (68, 34, 'geometric city'), (68, 35, 'metallic'), (68, 36, 'orderly'),
    -- Image 69
    (69, 34, 'dreamscape'), (69, 35, 'cloudy'), (69, 36, 'surreal'),

    -- [Prompt 14] 자연 요소 (변수: element, perspective, lighting, flora / ID: 37~40)
    -- 이미지: 70~73번
    -- Image 70 (Preview)
    (70, 37, 'crystal tree'), (70, 38, 'low angle'), (70, 39, 'bioluminescent'), (70, 40, 'glowing mushrooms'),
    -- Image 71
    (71, 37, 'waterfall'), (71, 38, 'birds eye view'), (71, 39, 'sunny morning'), (71, 40, 'mossy rocks'),
    -- Image 72
    (72, 37, 'volcano'), (72, 38, 'eye level'), (72, 39, 'dramatic red'), (72, 40, 'scorched earth'),
    -- Image 73
    (73, 37, 'ice cave'), (73, 38, 'close up'), (73, 39, 'cold blue'), (73, 40, 'frozen ferns'),

    -- [Prompt 15] 신화 생물 (변수: creature, power, wings, eye, habitat / ID: 41~45)
    -- 이미지: 74~80번
    -- Image 74 (Preview)
    (74, 41, 'phoenix'), (74, 42, 'fire'), (74, 43, 'burning feathers'), (74, 44, 'glowing gold'), (74, 45, 'volcano peak'),
    -- Image 75
    (75, 41, 'ice dragon'), (75, 42, 'frost'), (75, 43, 'crystalline'), (75, 44, 'piercing blue'), (75, 45, 'glacier cave'),
    -- Image 76
    (76, 41, 'storm griffin'), (76, 42, 'lightning'), (76, 43, 'thundercloud'), (76, 44, 'electric yellow'), (76, 45, 'mountain top'),
    -- Image 77
    (77, 41, 'forest spirit'), (77, 42, 'nature'), (77, 43, 'leaf-like'), (77, 44, 'emerald green'), (77, 45, 'ancient woods'),
    -- Image 78
    (78, 41, 'shadow demon'), (78, 42, 'darkness'), (78, 43, 'smoke'), (78, 44, 'crimson red'), (78, 45, 'abyss'),
    -- Image 79
    (79, 41, 'sea serpent'), (79, 42, 'water'), (79, 43, 'finned'), (79, 44, 'deep black'), (79, 45, 'ocean trench'),
    -- Image 80
    (80, 41, 'celestial unicorn'), (80, 42, 'starlight'), (80, 43, 'translucent'), (80, 44, 'silver'), (80, 45, 'cloudy sky');


-- Lookbook Image Variable Options (Prompt 16~20 Full Data)
INSERT IGNORE INTO lookbook_image_variable_options (lookbook_image_id, prompt_variable_id, variable_value) VALUES
    -- [Prompt 16] 시대 건축 (변수: period / ID: 46)
    -- 이미지: 81~82번
    (81, 46, 'victorian'),      -- Image 81 (Preview)
    (82, 46, 'futuristic'),     -- Image 82

    -- [Prompt 17] 직업 초상 (변수: profession, clothing / ID: 47, 48)
    -- 이미지: 83~87번
    -- Image 83 (Preview)
    (83, 47, 'astronaut'), (83, 48, 'white space suit'),
    -- Image 84
    (84, 47, 'chef'), (84, 48, 'apron'),
    -- Image 85
    (85, 47, 'firefighter'), (85, 48, 'turnout gear'),
    -- Image 86
    (86, 47, 'doctor'), (86, 48, 'white coat'),
    -- Image 87
    (87, 47, 'detective'), (87, 48, 'trench coat'),

    -- [Prompt 18] 날씨 풍경 (변수: weather, terrain, vegetation / ID: 49, 50, 51)
    -- 이미지: 88~95번
    -- Image 88 (Preview)
    (88, 49, 'heavy rain'), (88, 50, 'rocky mountains'), (88, 51, 'moss'),
    -- Image 89
    (89, 49, 'sunny'), (89, 50, 'desert dunes'), (89, 51, 'cactus'),
    -- Image 90
    (90, 49, 'snow storm'), (90, 50, 'tundra'), (90, 51, 'pine trees'),
    -- Image 91
    (91, 49, 'foggy'), (91, 50, 'swamp'), (91, 51, 'mangroves'),
    -- Image 92
    (92, 49, 'thunderstorm'), (92, 50, 'ocean cliff'), (92, 51, 'sparse grass'),
    -- Image 93
    (93, 49, 'cloudy'), (93, 50, 'grassy plains'), (93, 51, 'wildflowers'),
    -- Image 94
    (94, 49, 'misty'), (94, 50, 'jungle valley'), (94, 51, 'thick vines'),
    -- Image 95
    (95, 49, 'clear night'), (95, 50, 'canyon'), (95, 51, 'dry brush'),

    -- [Prompt 19] 음식 일러스트 (변수: food, plate, garnish, drink / ID: 52~55)
    -- 이미지: 96~99번
    -- Image 96 (Preview)
    (96, 52, 'steak'), (96, 53, 'black slate'), (96, 54, 'rosemary'), (96, 55, 'red wine'),
    -- Image 97
    (97, 52, 'sushi'), (97, 53, 'wooden boat'), (97, 54, 'pickled ginger'), (97, 55, 'sake'),
    -- Image 98
    (98, 52, 'burger'), (98, 53, 'red basket'), (98, 54, 'pickle'), (98, 55, 'cola'),
    -- Image 99
    (99, 52, 'pasta'), (99, 53, 'white bowl'), (99, 54, 'basil leaf'), (99, 55, 'sparkling water'),

    -- [Prompt 20] 기하학 패턴 (변수: pattern, color1, color2, line, complexity / ID: 56~60)
    -- 이미지: 100~105번
    -- Image 100 (Preview)
    (100, 56, 'geometric'), (100, 57, 'black'), (100, 58, 'white'), (100, 59, 'thin'), (100, 60, 'high'),
    -- Image 101
    (101, 56, 'floral'), (101, 57, 'pink'), (101, 58, 'green'), (101, 59, 'medium'), (101, 60, 'low'),
    -- Image 102
    (102, 56, 'abstract'), (102, 57, 'blue'), (102, 58, 'orange'), (102, 59, 'thick'), (102, 60, 'medium'),
    -- Image 103
    (103, 56, 'stripes'), (103, 57, 'red'), (103, 58, 'white'), (103, 59, 'very thick'), (103, 60, 'very low'),
    -- Image 104
    (104, 56, 'polka dot'), (104, 57, 'yellow'), (104, 58, 'black'), (104, 59, 'none'), (104, 60, 'simple'),
    -- Image 105
    (105, 56, 'fractal'), (105, 57, 'purple'), (105, 58, 'gold'), (105, 59, 'hairline'), (105, 60, 'extreme');

-- Purchases (10개)
INSERT IGNORE INTO purchases (purchase_id, user_id, prompt_id, price, purchased_at) VALUES
    (1, 11, 1, 5, NOW()),
    (2, 11, 2, 8, NOW()),
    (3, 12, 3, 5, NOW()),
    (4, 12, 4, 10, NOW()),
    (5, 13, 5, 5, NOW()),
    (6, 13, 6, 7, NOW()),
    (7, 14, 7, 5, NOW()),
    (8, 14, 8, 9, NOW()),
    (9, 15, 9, 5, NOW()),
    (10, 15, 10, 6, NOW());

-- Credit Charge Options (5개)
INSERT IGNORE INTO credit_charge_option (id, amount) VALUES
    (1, 3000),
    (2, 5000),
    (3, 10000),
    (4, 30000),
    (5, 50000);

-- Bonus Credit Policies (3개)
INSERT IGNORE INTO bonus_credit_policy (id, min_amount, bonus_rate, description) VALUES
    (1, 5000, 0.10, '10% Bonus'),
    (2, 10000, 0.15, '15% Bonus'),
    (3, 30000, 0.20, '20% Bonus');

-- 1. Generated Images (is_public 포함, 겹치지 않는 ID 사용)
INSERT IGNORE INTO generated_images (image_id, purchase_id, task_id, image_url, image_quality, status, is_public, created_at)
VALUES
    (10001, 1, 'task_final_10001', 'https://picsum.photos/seed/10001/600/400', '1K', 'COMPLETED', true, NOW()),
    (10002, 2, 'task_final_10002', 'https://picsum.photos/seed/10002/600/400', '1K', 'COMPLETED', false, NOW()),
    (10003, 3, 'task_final_10003', 'https://picsum.photos/seed/10003/600/400', '2K', 'COMPLETED', true, NOW());

-- 2. Generated Image Variable Values (variable_value 컬럼명 일치)
INSERT IGNORE INTO generated_image_variable_values (id, image_id, prompt_variable_id, variable_value)
VALUES
    (10001, 10001, 1, 'final test value 1'),
    (10002, 10002, 2, 'final test value 2'),
    (10003, 10002, 3, 'final test value 3');

-- 3. 판매 목록 테스트용 추가 프롬프트
INSERT IGNORE INTO prompts (prompt_id, title, price, status, user_id, category_id, model_id, is_deleted, preview_image_url, created_at) VALUES
                                                                                                                                            (101, '네온 사이버펑크', 10, 'APPROVED', 2, 1, 1, 0, 'https://picsum.photos/seed/neon/200/300', NOW()),
                                                                                                                                            (102, '중세 판타지 배경', 15, 'PENDING', 2, 1, 1, 0, 'https://picsum.photos/seed/fantasy/200/300', NOW());