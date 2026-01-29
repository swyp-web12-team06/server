-- ==========================================
-- REALISTIC IMAGE DUMMY DATA (Picsum Applied)
-- ==========================================

-- 1. Categories (기존 동일)
INSERT IGNORE INTO categories (category_id, name, order_index, is_active, created_at, updated_at)
VALUES
    (1, '인물', 1, true, NOW(), NOW()),
    (2, '풍경', 2, true, NOW(), NOW()),
    (3, '일러스트', 3, true, NOW(), NOW()),
    (4, '추상', 4, true, NOW(), NOW()),
    (5, '건축', 5, true, NOW(), NOW());

-- 2. AI Models (기존 동일)
INSERT IGNORE INTO ai_models (model_id, name, order_index, is_active, created_at, updated_at)
VALUES
    (1, 'grok-imagine/text-to-image', 1, true, NOW(), NOW()),
    (2, 'nano-banana-pro', 2, true, NOW(), NOW());

-- 3. Users (기존 동일)
INSERT IGNORE INTO users (user_id, email, nickname, role, credit_balance, is_banned, deleted_at, provider, provider_id, terms_agreed, marketing_consent, created_at, updated_at, warning_count)
VALUES
    (1, 'artist1@test.com', 'AI아티스트', 'SELLER', 10000, false, null, 'local', 'artist-001', true, false, NOW(), NOW(), 0),
    (2, 'creator2@test.com', '크리에이터Kim', 'SELLER', 5000, false, null, 'local', 'creator-002', true, true, NOW(), NOW(), 0),
    (3, 'designer3@test.com', '디자이너Park', 'SELLER', 8000, false, null, 'local', 'designer-003', true, false, NOW(), NOW(), 0),
    (4, 'prompter4@test.com', 'PromptMaster', 'SELLER', 12000, false, null, 'local', 'prompter-004', true, true, NOW(), NOW(), 0),
    (5, 'user5@test.com', '그림쟁이', 'SELLER', 3000, false, null, 'local', 'user-005', true, false, NOW(), NOW(), 0),
    (6, 'maker6@test.com', 'ImageMaker', 'SELLER', 7000, false, null, 'local', 'maker-006', true, false, NOW(), NOW(), 0),
    (7, 'artist7@test.com', '예술가Lee', 'SELLER', 9000, false, null, 'local', 'artist-007', true, true, NOW(), NOW(), 0),
    (8, 'pro8@test.com', 'ProDesigner', 'SELLER', 15000, false, null, 'local', 'pro-008', true, false, NOW(), NOW(), 0),
    (9, 'creative9@test.com', '창작자Choi', 'SELLER', 6000, false, null, 'local', 'creative-009', true, false, NOW(), NOW(), 0),
    (10, 'pixel10@test.com', 'PixelArtist', 'SELLER', 4000, false, null, 'local', 'pixel-010', true, true, NOW(), NOW(), 0),
    (11, 'digital11@test.com', '디지털작가', 'SELLER', 11000, false, null, 'local', 'digital-011', true, false, NOW(), NOW(), 0),
    (12, 'cyber12@test.com', 'CyberCreator', 'SELLER', 8500, false, null, 'local', 'cyber-012', true, true, NOW(), NOW(), 0),
    (13, 'neon13@test.com', 'NeonDreamer', 'SELLER', 7500, false, null, 'local', 'neon-013', true, false, NOW(), NOW(), 0),
    (14, 'fantasy14@test.com', 'FantasyMaker', 'SELLER', 9500, false, null, 'local', 'fantasy-014', true, false, NOW(), NOW(), 0),
    (15, 'sci15@test.com', 'SciFiArtist', 'SELLER', 10500, false, null, 'local', 'scifi-015', true, true, NOW(), NOW(), 0),
    (16, 'nature16@test.com', '자연화가', 'SELLER', 6500, false, null, 'local', 'nature-016', true, false, NOW(), NOW(), 0),
    (17, 'urban17@test.com', 'UrbanSketch', 'SELLER', 7200, false, null, 'local', 'urban-017', true, false, NOW(), NOW(), 0),
    (18, 'retro18@test.com', 'RetroVibe', 'SELLER', 8200, false, null, 'local', 'retro-018', true, true, NOW(), NOW(), 0),
    (19, 'minimal19@test.com', 'MinimalDesign', 'SELLER', 9200, false, null, 'local', 'minimal-019', true, false, NOW(), NOW(), 0),
    (20, 'color20@test.com', 'ColorMaster', 'SELLER', 10200, false, null, 'local', 'color-020', true, true, NOW(), NOW(), 0),
    (21, 'gothic21@test.com', 'GothicStyle', 'SELLER', 7800, false, null, 'local', 'gothic-021', true, false, NOW(), NOW(), 0),
    (22, 'anime22@test.com', 'AnimeDrawer', 'SELLER', 6300, false, null, 'local', 'anime-022', true, true, NOW(), NOW(), 0),
    (23, 'sketch23@test.com', 'SketchMaster', 'SELLER', 5500, false, null, 'local', 'sketch-023', true, false, NOW(), NOW(), 0),
    (24, 'photo24@test.com', 'PhotoRealist', 'SELLER', 12500, false, null, 'local', 'photo-024', true, false, NOW(), NOW(), 0),
    (25, 'water25@test.com', 'WaterColor', 'SELLER', 4800, false, null, 'local', 'water-025', true, true, NOW(), NOW(), 0),
    (26, 'oil26@test.com', 'OilPainting', 'SELLER', 9800, false, null, 'local', 'oil-026', true, false, NOW(), NOW(), 0),
    (27, 'comic27@test.com', 'ComicArtist', 'SELLER', 7100, false, null, 'local', 'comic-027', true, true, NOW(), NOW(), 0),
    (28, 'manga28@test.com', 'MangaMaker', 'SELLER', 8900, false, null, 'local', 'manga-028', true, false, NOW(), NOW(), 0),
    (29, 'vector29@test.com', 'VectorDesign', 'SELLER', 6700, false, null, 'local', 'vector-029', true, false, NOW(), NOW(), 0),
    (30, '3d30@test.com', '3DArtist', 'SELLER', 11500, false, null, 'local', '3d-030', true, true, NOW(), NOW(), 0),
    (31, 'concept31@test.com', 'ConceptArt', 'SELLER', 10800, false, null, 'local', 'concept-031', true, false, NOW(), NOW(), 0),
    (32, 'matte32@test.com', 'MattePaint', 'SELLER', 9300, false, null, 'local', 'matte-032', true, true, NOW(), NOW(), 0),
    (33, 'surreal33@test.com', 'SurrealArt', 'SELLER', 8700, false, null, 'local', 'surreal-033', true, false, NOW(), NOW(), 0),
    (34, 'baroque34@test.com', 'BaroqueStyle', 'SELLER', 7400, false, null, 'local', 'baroque-034', true, false, NOW(), NOW(), 0),
    (35, 'modern35@test.com', 'ModernArt', 'SELLER', 6900, false, null, 'local', 'modern-035', true, true, NOW(), NOW(), 0),
    (36, 'classic36@test.com', 'ClassicPaint', 'SELLER', 10300, false, null, 'local', 'classic-036', true, false, NOW(), NOW(), 0),
    (37, 'pop37@test.com', 'PopArtist', 'SELLER', 7600, false, null, 'local', 'pop-037', true, true, NOW(), NOW(), 0),
    (38, 'street38@test.com', 'StreetArt', 'SELLER', 5900, false, null, 'local', 'street-038', true, false, NOW(), NOW(), 0),
    (39, 'graffiti39@test.com', 'GraffitiKing', 'SELLER', 6400, false, null, 'local', 'graffiti-039', true, false, NOW(), NOW(), 0),
    (40, 'fine40@test.com', 'FineArt', 'SELLER', 13000, false, null, 'local', 'fine-040', true, true, NOW(), NOW(), 0),
    (41, 'pastel41@test.com', 'PastelDream', 'SELLER', 5200, false, null, 'local', 'pastel-041', true, false, NOW(), NOW(), 0),
    (42, 'charcoal42@test.com', 'CharcoalDraw', 'SELLER', 4500, false, null, 'local', 'charcoal-042', true, true, NOW(), NOW(), 0),
    (43, 'ink43@test.com', 'InkMaster', 'SELLER', 7300, false, null, 'local', 'ink-043', true, false, NOW(), NOW(), 0),
    (44, 'acrylic44@test.com', 'AcrylicArt', 'SELLER', 8100, false, null, 'local', 'acrylic-044', true, false, NOW(), NOW(), 0),
    (45, 'spray45@test.com', 'SprayPaint', 'SELLER', 5700, false, null, 'local', 'spray-045', true, true, NOW(), NOW(), 0),
    (46, 'brush46@test.com', 'BrushStroke', 'SELLER', 9400, false, null, 'local', 'brush-046', true, false, NOW(), NOW(), 0),
    (47, 'palette47@test.com', 'PaletteKnife', 'SELLER', 6100, false, null, 'local', 'palette-047', true, true, NOW(), NOW(), 0),
    (48, 'canvas48@test.com', 'CanvasPro', 'SELLER', 10700, false, null, 'local', 'canvas-048', true, false, NOW(), NOW(), 0),
    (49, 'studio49@test.com', 'StudioWork', 'SELLER', 8800, false, null, 'local', 'studio-049', true, false, NOW(), NOW(), 0),
    (50, 'gallery50@test.com', 'GalleryArt', 'SELLER', 12200, false, null, 'local', 'gallery-050', true, true, NOW(), NOW(), 0);

-- 4. Prompts (200개 - Picsum Seed URL 적용)
INSERT IGNORE INTO prompts (prompt_id, user_id, category_id, model_id, title, description, price, master_prompt, status, preview_image_url, is_deleted, created_at, updated_at)
VALUES
-- 사이버펑크 & 네온 테마
(1, 1, 1, 1, '사이버펑크 고양이', 'Neon-lit cyberpunk cat', 500, 'cyberpunk cat, neon', 'APPROVED', 'https://picsum.photos/seed/1/300/300', false, NOW(), NOW()),
(2, 1, 3, 2, '네온 고양이 일러스트', 'Glowing neon cat', 800, 'neon cat, glowing', 'APPROVED', 'https://picsum.photos/seed/2/300/300', false, NOW(), NOW()),
(3, 2, 1, 1, '사이버펑크 전사', 'Cyberpunk warrior', 1200, 'cyberpunk warrior', 'APPROVED', 'https://picsum.photos/seed/3/300/300', false, NOW(), NOW()),
(4, 2, 2, 2, '미래 도시 풍경', 'Futuristic city', 1500, 'futuristic city', 'APPROVED', 'https://picsum.photos/seed/4/300/300', false, NOW(), NOW()),
(5, 3, 3, 1, '사이버 드래곤', 'Mechanical cyber dragon', 2000, 'mechanical dragon', 'APPROVED', 'https://picsum.photos/seed/5/300/300', false, NOW(), NOW()),
(6, 3, 1, 2, '네온 인물 초상화', 'Portrait neon', 1800, 'portrait, neon', 'APPROVED', 'https://picsum.photos/seed/6/300/300', false, NOW(), NOW()),
(7, 4, 2, 1, '사이버펑크 거리', 'Cyberpunk street', 1100, 'cyberpunk street', 'APPROVED', 'https://picsum.photos/seed/7/300/300', false, NOW(), NOW()),
(8, 4, 4, 2, '네온 추상', 'Neon abstract', 900, 'neon abstract', 'APPROVED', 'https://picsum.photos/seed/8/300/300', false, NOW(), NOW()),
(9, 5, 3, 1, '사이버 강아지', 'Cyberpunk dog', 700, 'cyberpunk dog', 'APPROVED', 'https://picsum.photos/seed/9/300/300', false, NOW(), NOW()),
(10, 5, 1, 2, '네온 로봇', 'Neon robot', 1600, 'neon robot', 'APPROVED', 'https://picsum.photos/seed/10/300/300', false, NOW(), NOW()),

-- 자연 & 풍경 테마
(11, 6, 2, 1, '수채화 풍경', 'Watercolor landscape', 0, 'watercolor landscape', 'APPROVED', 'https://picsum.photos/seed/11/300/300', false, NOW(), NOW()),
(12, 6, 2, 2, '자연 풍경 사진', 'Natural landscape', 600, 'natural landscape', 'APPROVED', 'https://picsum.photos/seed/12/300/300', false, NOW(), NOW()),
(13, 7, 2, 1, '숲속 오두막', 'Cabin in forest', 900, 'cabin, forest', 'APPROVED', 'https://picsum.photos/seed/13/300/300', false, NOW(), NOW()),
(14, 7, 2, 2, '벚꽃 풍경', 'Cherry blossom', 1100, 'cherry blossom', 'APPROVED', 'https://picsum.photos/seed/14/300/300', false, NOW(), NOW()),
(15, 8, 2, 1, '가을 단풍', 'Autumn forest', 700, 'autumn forest', 'APPROVED', 'https://picsum.photos/seed/15/300/300', false, NOW(), NOW()),
(16, 8, 2, 2, '숲 속 길', 'Forest path', 850, 'forest path', 'APPROVED', 'https://picsum.photos/seed/16/300/300', false, NOW(), NOW()),
(17, 9, 2, 1, '자연 계곡', 'Valley view', 950, 'valley, river', 'APPROVED', 'https://picsum.photos/seed/17/300/300', false, NOW(), NOW()),
(18, 9, 2, 2, '숲 호수', 'Forest lake', 1050, 'lake, forest', 'APPROVED', 'https://picsum.photos/seed/18/300/300', false, NOW(), NOW()),
(19, 10, 2, 1, '자연 일몰', 'Sunset scene', 800, 'sunset', 'APPROVED', 'https://picsum.photos/seed/19/300/300', false, NOW(), NOW()),
(20, 10, 2, 2, '숲 안개', 'Misty forest', 900, 'misty forest', 'APPROVED', 'https://picsum.photos/seed/20/300/300', false, NOW(), NOW()),

-- 인물 초상화
(21, 11, 1, 1, 'AI 초상화', 'Realistic portrait', 1800, 'realistic portrait', 'APPROVED', 'https://picsum.photos/seed/21/300/300', false, NOW(), NOW()),
(22, 11, 1, 2, '판타지 인물', 'Fantasy character', 2200, 'fantasy character', 'APPROVED', 'https://picsum.photos/seed/22/300/300', false, NOW(), NOW()),
(23, 12, 1, 1, '현대 인물 초상', 'Modern portrait', 1600, 'modern portrait', 'APPROVED', 'https://picsum.photos/seed/23/300/300', false, NOW(), NOW()),
(24, 12, 1, 2, '빈티지 초상화', 'Vintage portrait', 1400, 'vintage portrait', 'APPROVED', 'https://picsum.photos/seed/24/300/300', false, NOW(), NOW()),
(25, 13, 1, 1, '드라마틱 인물', 'Dramatic portrait', 2500, 'dramatic portrait', 'APPROVED', 'https://picsum.photos/seed/25/300/300', false, NOW(), NOW()),
(26, 13, 1, 2, '여성 초상화', 'Elegant woman', 1850, 'elegant woman', 'APPROVED', 'https://picsum.photos/seed/26/300/300', false, NOW(), NOW()),
(27, 14, 1, 1, '남성 초상화', 'Masculine portrait', 1750, 'masculine portrait', 'APPROVED', 'https://picsum.photos/seed/27/300/300', false, NOW(), NOW()),
(28, 14, 1, 2, '아이 초상화', 'Child portrait', 1300, 'child portrait', 'APPROVED', 'https://picsum.photos/seed/28/300/300', false, NOW(), NOW()),
(29, 15, 1, 1, '노인 초상화', 'Elder portrait', 1900, 'elder portrait', 'APPROVED', 'https://picsum.photos/seed/29/300/300', false, NOW(), NOW()),
(30, 15, 1, 2, '커플 초상화', 'Couple portrait', 2100, 'couple portrait', 'APPROVED', 'https://picsum.photos/seed/30/300/300', false, NOW(), NOW()),

-- 추상 & 예술
(31, 16, 4, 1, '추상 미술', 'Abstract art', 1000, 'abstract shapes', 'APPROVED', 'https://picsum.photos/seed/31/300/300', false, NOW(), NOW()),
(32, 16, 4, 2, '기하학 패턴', 'Geometric pattern', 800, 'geometric patterns', 'APPROVED', 'https://picsum.photos/seed/32/300/300', false, NOW(), NOW()),
(33, 17, 4, 1, '유기적 형태', 'Organic abstract', 1300, 'organic shapes', 'APPROVED', 'https://picsum.photos/seed/33/300/300', false, NOW(), NOW()),
(34, 17, 4, 2, '미니멀 추상', 'Minimal abstract', 900, 'minimal abstract', 'APPROVED', 'https://picsum.photos/seed/34/300/300', false, NOW(), NOW()),
(35, 18, 4, 1, '컬러 스플래시', 'Color splash', 1100, 'color splash', 'APPROVED', 'https://picsum.photos/seed/35/300/300', false, NOW(), NOW()),
(36, 18, 4, 2, '추상 예술', 'Abstract expressionism', 1500, 'abstract art', 'APPROVED', 'https://picsum.photos/seed/36/300/300', false, NOW(), NOW()),
(37, 19, 4, 1, '기하학 추상', 'Geometric abstraction', 950, 'geometric', 'APPROVED', 'https://picsum.photos/seed/37/300/300', false, NOW(), NOW()),
(38, 19, 4, 2, '추상 풍경', 'Abstract landscape', 1050, 'abstract landscape', 'APPROVED', 'https://picsum.photos/seed/38/300/300', false, NOW(), NOW()),
(39, 20, 4, 1, '추상 인물', 'Abstract portrait', 1250, 'abstract portrait', 'APPROVED', 'https://picsum.photos/seed/39/300/300', false, NOW(), NOW()),
(40, 20, 4, 2, '현대 추상', 'Contemporary abstract', 1150, 'contemporary abstract', 'APPROVED', 'https://picsum.photos/seed/40/300/300', false, NOW(), NOW()),

-- 건축
(41, 21, 5, 1, '모던 건축', 'Modern architecture', 1700, 'modern building', 'APPROVED', 'https://picsum.photos/seed/41/300/300', false, NOW(), NOW()),
(42, 21, 5, 2, '고딕 성당', 'Gothic cathedral', 1900, 'gothic cathedral', 'APPROVED', 'https://picsum.photos/seed/42/300/300', false, NOW(), NOW()),
(43, 22, 5, 1, '미래 건축물', 'Futuristic architecture', 2100, 'futuristic building', 'APPROVED', 'https://picsum.photos/seed/43/300/300', false, NOW(), NOW()),
(44, 22, 5, 2, '전통 한옥', 'Traditional Hanok', 1500, 'traditional hanok', 'APPROVED', 'https://picsum.photos/seed/44/300/300', false, NOW(), NOW()),
(45, 23, 5, 1, '도시 야경', 'City night', 1300, 'city skyline', 'APPROVED', 'https://picsum.photos/seed/45/300/300', false, NOW(), NOW()),
(46, 23, 5, 2, '고대 신전', 'Ancient temple', 1650, 'ancient temple', 'APPROVED', 'https://picsum.photos/seed/46/300/300', false, NOW(), NOW()),
(47, 24, 5, 1, '교량 건축', 'Bridge architecture', 1400, 'modern bridge', 'APPROVED', 'https://picsum.photos/seed/47/300/300', false, NOW(), NOW()),
(48, 24, 5, 2, '마천루', 'Skyscraper', 1800, 'skyscraper', 'APPROVED', 'https://picsum.photos/seed/48/300/300', false, NOW(), NOW()),
(49, 25, 5, 1, '전통 건축', 'Traditional architecture', 1200, 'traditional building', 'APPROVED', 'https://picsum.photos/seed/49/300/300', false, NOW(), NOW()),
(50, 25, 5, 2, '현대 건물', 'Contemporary building', 1600, 'glass building', 'APPROVED', 'https://picsum.photos/seed/50/300/300', false, NOW(), NOW()),

-- 동물 일러스트 (51-60)
(51, 26, 3, 1, '귀여운 강아지', 'Cute puppy', 500, 'cute puppy', 'APPROVED', 'https://picsum.photos/seed/51/300/300', false, NOW(), NOW()),
(52, 26, 3, 2, '고양이 캐릭터', 'Cat character', 600, 'cat character', 'APPROVED', 'https://picsum.photos/seed/52/300/300', false, NOW(), NOW()),
(53, 27, 3, 1, '강아지 일러스트', 'Dog illustration', 550, 'dog', 'APPROVED', 'https://picsum.photos/seed/53/300/300', false, NOW(), NOW()),
(54, 27, 3, 2, '고양이 그림', 'Cat drawing', 650, 'cat', 'APPROVED', 'https://picsum.photos/seed/54/300/300', false, NOW(), NOW()),
(55, 28, 3, 1, '펫 일러스트', 'Pet illustration', 700, 'pet', 'APPROVED', 'https://picsum.photos/seed/55/300/300', false, NOW(), NOW()),
(56, 28, 3, 2, '동물 캐릭터', 'Animal character', 750, 'animal', 'APPROVED', 'https://picsum.photos/seed/56/300/300', false, NOW(), NOW()),
(57, 29, 3, 1, '강아지 초상', 'Dog portrait', 800, 'dog portrait', 'APPROVED', 'https://picsum.photos/seed/57/300/300', false, NOW(), NOW()),
(58, 29, 3, 2, '고양이 초상', 'Cat portrait', 850, 'cat portrait', 'APPROVED', 'https://picsum.photos/seed/58/300/300', false, NOW(), NOW()),
(59, 30, 3, 1, '야생동물', 'Wild animal', 900, 'wild animal', 'APPROVED', 'https://picsum.photos/seed/59/300/300', false, NOW(), NOW()),
(60, 30, 3, 2, '동물 일러스트', 'Animal illustration', 600, 'animal art', 'APPROVED', 'https://picsum.photos/seed/60/300/300', false, NOW(), NOW()),

-- 판타지 (61-70)
(61, 31, 3, 1, '드래곤 일러스트', 'Dragon illustration', 1750, 'dragon', 'APPROVED', 'https://picsum.photos/seed/61/300/300', false, NOW(), NOW()),
(62, 31, 1, 2, '마법사 초상화', 'Wizard portrait', 1650, 'wizard', 'APPROVED', 'https://picsum.photos/seed/62/300/300', false, NOW(), NOW()),
(63, 32, 3, 1, '유니콘 일러스트', 'Unicorn illustration', 1450, 'unicorn', 'APPROVED', 'https://picsum.photos/seed/63/300/300', false, NOW(), NOW()),
(64, 32, 1, 2, '엘프 초상화', 'Elf portrait', 1450, 'elf', 'APPROVED', 'https://picsum.photos/seed/64/300/300', false, NOW(), NOW()),
(65, 33, 3, 1, '불사조 일러스트', 'Phoenix illustration', 1550, 'phoenix', 'APPROVED', 'https://picsum.photos/seed/65/300/300', false, NOW(), NOW()),
(66, 33, 1, 2, '요정 초상화', 'Fairy portrait', 850, 'fairy', 'APPROVED', 'https://picsum.photos/seed/66/300/300', false, NOW(), NOW()),
(67, 34, 3, 1, '판타지 크리처', 'Fantasy creature', 1350, 'creature', 'APPROVED', 'https://picsum.photos/seed/67/300/300', false, NOW(), NOW()),
(68, 34, 1, 2, '판타지 전사', 'Fantasy warrior', 1750, 'warrior', 'APPROVED', 'https://picsum.photos/seed/68/300/300', false, NOW(), NOW()),
(69, 35, 3, 1, '마법 동물', 'Magical animal', 950, 'magic animal', 'APPROVED', 'https://picsum.photos/seed/69/300/300', false, NOW(), NOW()),
(70, 35, 1, 2, '판타지 인물', 'Fantasy character', 1850, 'fantasy char', 'APPROVED', 'https://picsum.photos/seed/70/300/300', false, NOW(), NOW()),

-- 우주 (71-80)
(71, 36, 2, 1, '우주 풍경', 'Space landscape', 2000, 'space', 'APPROVED', 'https://picsum.photos/seed/71/300/300', false, NOW(), NOW()),
(72, 36, 1, 2, '우주 비행사', 'Astronaut portrait', 1800, 'astronaut', 'APPROVED', 'https://picsum.photos/seed/72/300/300', false, NOW(), NOW()),
(73, 37, 2, 1, '외계 행성', 'Alien planet', 2000, 'alien planet', 'APPROVED', 'https://picsum.photos/seed/73/300/300', false, NOW(), NOW()),
(74, 37, 4, 2, '우주 추상', 'Space abstract', 1000, 'space art', 'APPROVED', 'https://picsum.photos/seed/74/300/300', false, NOW(), NOW()),
(75, 38, 2, 1, '은하수', 'Milky way', 1200, 'milky way', 'APPROVED', 'https://picsum.photos/seed/75/300/300', false, NOW(), NOW()),
(76, 38, 4, 2, '은하 추상', 'Galaxy abstract', 950, 'galaxy', 'APPROVED', 'https://picsum.photos/seed/76/300/300', false, NOW(), NOW()),
(77, 39, 2, 1, '별이 빛나는 밤', 'Starry night', 1100, 'starry night', 'APPROVED', 'https://picsum.photos/seed/77/300/300', false, NOW(), NOW()),
(78, 39, 1, 2, '우주 탐험가', 'Space explorer', 1650, 'explorer', 'APPROVED', 'https://picsum.photos/seed/78/300/300', false, NOW(), NOW()),
(79, 40, 4, 1, '우주 소용돌이', 'Space vortex', 1150, 'vortex', 'APPROVED', 'https://picsum.photos/seed/79/300/300', false, NOW(), NOW()),
(80, 40, 2, 2, '별자리', 'Constellation', 900, 'constellation', 'APPROVED', 'https://picsum.photos/seed/80/300/300', false, NOW(), NOW()),

-- 해양 (81-90)
(81, 41, 2, 1, '해변 일몰', 'Beach sunset', 700, 'beach', 'APPROVED', 'https://picsum.photos/seed/81/300/300', false, NOW(), NOW()),
(82, 41, 2, 2, '열대 해변', 'Tropical beach', 750, 'tropical', 'APPROVED', 'https://picsum.photos/seed/82/300/300', false, NOW(), NOW()),
(83, 42, 2, 1, '바다 풍경', 'Ocean view', 650, 'ocean', 'APPROVED', 'https://picsum.photos/seed/83/300/300', false, NOW(), NOW()),
(84, 42, 2, 2, '폭포 풍경', 'Waterfall', 950, 'waterfall', 'APPROVED', 'https://picsum.photos/seed/84/300/300', false, NOW(), NOW()),
(85, 43, 2, 1, '호수 풍경', 'Lake landscape', 750, 'lake', 'APPROVED', 'https://picsum.photos/seed/85/300/300', false, NOW(), NOW()),
(86, 43, 2, 2, '강 풍경', 'River landscape', 650, 'river', 'APPROVED', 'https://picsum.photos/seed/86/300/300', false, NOW(), NOW()),
(87, 44, 4, 1, '물결 추상', 'Water ripple', 850, 'water ripple', 'APPROVED', 'https://picsum.photos/seed/87/300/300', false, NOW(), NOW()),
(88, 44, 2, 2, '수중 풍경', 'Underwater', 1200, 'underwater', 'APPROVED', 'https://picsum.photos/seed/88/300/300', false, NOW(), NOW()),
(89, 45, 2, 1, '해안 절벽', 'Coastal cliff', 800, 'cliff', 'APPROVED', 'https://picsum.photos/seed/89/300/300', false, NOW(), NOW()),
(90, 45, 2, 2, '바다 일출', 'Ocean sunrise', 700, 'sunrise', 'APPROVED', 'https://picsum.photos/seed/90/300/300', false, NOW(), NOW()),

-- 도시 (91-100)
(91, 46, 5, 1, '도시 풍경', 'City skyline', 1300, 'skyline', 'APPROVED', 'https://picsum.photos/seed/91/300/300', false, NOW(), NOW()),
(92, 46, 2, 2, '거리 풍경', 'Street scene', 900, 'street', 'APPROVED', 'https://picsum.photos/seed/92/300/300', false, NOW(), NOW()),
(93, 47, 5, 1, '고층 빌딩', 'Skyscraper', 1650, 'skyscraper', 'APPROVED', 'https://picsum.photos/seed/93/300/300', false, NOW(), NOW()),
(94, 47, 2, 2, '도시 야경', 'City night', 1100, 'night city', 'APPROVED', 'https://picsum.photos/seed/94/300/300', false, NOW(), NOW()),
(95, 48, 2, 1, '거리 카페', 'Street cafe', 750, 'cafe', 'APPROVED', 'https://picsum.photos/seed/95/300/300', false, NOW(), NOW()),
(96, 48, 5, 2, '현대 도시', 'Modern city', 1400, 'modern city', 'APPROVED', 'https://picsum.photos/seed/96/300/300', false, NOW(), NOW()),
(97, 49, 2, 1, '골목길', 'Alley scene', 650, 'alley', 'APPROVED', 'https://picsum.photos/seed/97/300/300', false, NOW(), NOW()),
(98, 49, 5, 2, '광장', 'City plaza', 1200, 'plaza', 'APPROVED', 'https://picsum.photos/seed/98/300/300', false, NOW(), NOW()),
(99, 50, 2, 1, '도시 공원', 'Urban park', 800, 'park', 'APPROVED', 'https://picsum.photos/seed/99/300/300', false, NOW(), NOW()),
(100, 50, 5, 2, '도시 건축', 'Urban arch', 1500, 'architecture', 'APPROVED', 'https://picsum.photos/seed/100/300/300', false, NOW(), NOW()),

-- 계절 (101-110)
(101, 1, 2, 1, '봄 풍경', 'Spring landscape', 650, 'spring', 'APPROVED', 'https://picsum.photos/seed/101/300/300', false, NOW(), NOW()),
(102, 2, 2, 2, '여름 해변', 'Summer beach', 700, 'summer', 'APPROVED', 'https://picsum.photos/seed/102/300/300', false, NOW(), NOW()),
(103, 3, 2, 1, '가을 숲', 'Autumn forest', 750, 'autumn', 'APPROVED', 'https://picsum.photos/seed/103/300/300', false, NOW(), NOW()),
(104, 4, 2, 2, '겨울 설경', 'Winter snow', 800, 'snow', 'APPROVED', 'https://picsum.photos/seed/104/300/300', false, NOW(), NOW()),
(105, 5, 2, 1, '봄꽃', 'Spring flowers', 600, 'flowers', 'APPROVED', 'https://picsum.photos/seed/105/300/300', false, NOW(), NOW()),
(106, 6, 2, 2, '여름 정원', 'Summer garden', 650, 'garden', 'APPROVED', 'https://picsum.photos/seed/106/300/300', false, NOW(), NOW()),
(107, 7, 2, 1, '가을 단풍길', 'Autumn path', 700, 'leaves', 'APPROVED', 'https://picsum.photos/seed/107/300/300', false, NOW(), NOW()),
(108, 8, 2, 2, '겨울 마을', 'Winter village', 850, 'village', 'APPROVED', 'https://picsum.photos/seed/108/300/300', false, NOW(), NOW()),
(109, 9, 2, 1, '봄비', 'Spring rain', 550, 'rain', 'APPROVED', 'https://picsum.photos/seed/109/300/300', false, NOW(), NOW()),
(110, 10, 2, 2, '여름 해질녘', 'Summer twilight', 750, 'twilight', 'APPROVED', 'https://picsum.photos/seed/110/300/300', false, NOW(), NOW()),

-- 음식 (111-120)
(111, 11, 3, 1, '음식 일러스트', 'Food illustration', 500, 'food', 'APPROVED', 'https://picsum.photos/seed/111/300/300', false, NOW(), NOW()),
(112, 12, 3, 2, '과일 그림', 'Fruit painting', 550, 'fruit', 'APPROVED', 'https://picsum.photos/seed/112/300/300', false, NOW(), NOW()),
(113, 13, 3, 1, '디저트 일러스트', 'Dessert illustration', 600, 'dessert', 'APPROVED', 'https://picsum.photos/seed/113/300/300', false, NOW(), NOW()),
(114, 14, 3, 2, '커피 아트', 'Coffee art', 450, 'coffee', 'APPROVED', 'https://picsum.photos/seed/114/300/300', false, NOW(), NOW()),
(115, 15, 3, 1, '빵 일러스트', 'Bread illustration', 500, 'bread', 'APPROVED', 'https://picsum.photos/seed/115/300/300', false, NOW(), NOW()),
(116, 16, 3, 2, '채소 그림', 'Vegetable painting', 550, 'vegetable', 'APPROVED', 'https://picsum.photos/seed/116/300/300', false, NOW(), NOW()),
(117, 17, 3, 1, '요리 일러스트', 'Cooking illustration', 650, 'cooking', 'APPROVED', 'https://picsum.photos/seed/117/300/300', false, NOW(), NOW()),
(118, 18, 3, 2, '음료 일러스트', 'Beverage illustration', 500, 'drink', 'APPROVED', 'https://picsum.photos/seed/118/300/300', false, NOW(), NOW()),
(119, 19, 3, 1, '케이크 일러스트', 'Cake illustration', 600, 'cake', 'APPROVED', 'https://picsum.photos/seed/119/300/300', false, NOW(), NOW()),
(120, 20, 3, 2, '과일 바구니', 'Fruit basket', 700, 'basket', 'APPROVED', 'https://picsum.photos/seed/120/300/300', false, NOW(), NOW()),

-- 감정 (121-130)
(121, 21, 1, 1, '행복한 표정', 'Happy expression', 800, 'happy', 'APPROVED', 'https://picsum.photos/seed/121/300/300', false, NOW(), NOW()),
(122, 22, 4, 2, '평화로운 분위기', 'Peaceful mood', 900, 'peaceful', 'APPROVED', 'https://picsum.photos/seed/122/300/300', false, NOW(), NOW()),
(123, 23, 1, 1, '슬픈 감정', 'Sad emotion', 850, 'sad', 'APPROVED', 'https://picsum.photos/seed/123/300/300', false, NOW(), NOW()),
(124, 24, 4, 2, '역동적 에너지', 'Dynamic energy', 1000, 'dynamic', 'APPROVED', 'https://picsum.photos/seed/124/300/300', false, NOW(), NOW()),
(125, 25, 1, 1, '사색하는 인물', 'Contemplative', 950, 'thinking', 'APPROVED', 'https://picsum.photos/seed/125/300/300', false, NOW(), NOW()),
(126, 26, 4, 2, '명상적 추상', 'Meditative', 850, 'meditate', 'APPROVED', 'https://picsum.photos/seed/126/300/300', false, NOW(), NOW()),
(127, 27, 1, 1, '즐거운 순간', 'Joyful moment', 800, 'joy', 'APPROVED', 'https://picsum.photos/seed/127/300/300', false, NOW(), NOW()),
(128, 28, 4, 2, '고요한 분위기', 'Tranquil mood', 750, 'calm', 'APPROVED', 'https://picsum.photos/seed/128/300/300', false, NOW(), NOW()),
(129, 29, 1, 1, '열정적 표현', 'Passionate', 1100, 'passion', 'APPROVED', 'https://picsum.photos/seed/129/300/300', false, NOW(), NOW()),
(130, 30, 4, 2, '신비로운 분위기', 'Mysterious', 950, 'mystery', 'APPROVED', 'https://picsum.photos/seed/130/300/300', false, NOW(), NOW()),

-- 패션 (131-140)
(131, 31, 1, 1, '패션 모델', 'Fashion model', 1200, 'model', 'APPROVED', 'https://picsum.photos/seed/131/300/300', false, NOW(), NOW()),
(132, 32, 1, 2, '한복 스타일', 'Hanbok style', 1100, 'hanbok', 'APPROVED', 'https://picsum.photos/seed/132/300/300', false, NOW(), NOW()),
(133, 33, 1, 1, '현대 패션', 'Modern fashion', 1000, 'fashion', 'APPROVED', 'https://picsum.photos/seed/133/300/300', false, NOW(), NOW()),
(134, 34, 1, 2, '빈티지 패션', 'Vintage fashion', 950, 'vintage', 'APPROVED', 'https://picsum.photos/seed/134/300/300', false, NOW(), NOW()),
(135, 35, 1, 1, '스트리트 패션', 'Street fashion', 900, 'street style', 'APPROVED', 'https://picsum.photos/seed/135/300/300', false, NOW(), NOW()),
(136, 36, 1, 2, '럭셔리 스타일', 'Luxury style', 1500, 'luxury', 'APPROVED', 'https://picsum.photos/seed/136/300/300', false, NOW(), NOW()),
(137, 37, 1, 1, '캐주얼 룩', 'Casual look', 700, 'casual', 'APPROVED', 'https://picsum.photos/seed/137/300/300', false, NOW(), NOW()),
(138, 38, 1, 2, '이브닝 드레스', 'Evening dress', 1300, 'dress', 'APPROVED', 'https://picsum.photos/seed/138/300/300', false, NOW(), NOW()),
(139, 39, 1, 1, '스포츠 웨어', 'Sports wear', 800, 'sportswear', 'APPROVED', 'https://picsum.photos/seed/139/300/300', false, NOW(), NOW()),
(140, 40, 1, 2, '보헤미안 스타일', 'Bohemian style', 850, 'boho', 'APPROVED', 'https://picsum.photos/seed/140/300/300', false, NOW(), NOW()),

-- 직업 (141-150)
(141, 41, 1, 1, '의사 초상', 'Doctor portrait', 1100, 'doctor', 'APPROVED', 'https://picsum.photos/seed/141/300/300', false, NOW(), NOW()),
(142, 42, 1, 2, '선생님 일러스트', 'Teacher illustration', 900, 'teacher', 'APPROVED', 'https://picsum.photos/seed/142/300/300', false, NOW(), NOW()),
(143, 43, 1, 1, '요리사 초상', 'Chef portrait', 1000, 'chef', 'APPROVED', 'https://picsum.photos/seed/143/300/300', false, NOW(), NOW()),
(144, 44, 1, 2, '예술가 작업', 'Artist working', 950, 'artist', 'APPROVED', 'https://picsum.photos/seed/144/300/300', false, NOW(), NOW()),
(145, 45, 1, 1, '음악가 연주', 'Musician playing', 1050, 'musician', 'APPROVED', 'https://picsum.photos/seed/145/300/300', false, NOW(), NOW()),
(146, 46, 1, 2, '운동선수', 'Athlete', 1150, 'athlete', 'APPROVED', 'https://picsum.photos/seed/146/300/300', false, NOW(), NOW()),
(147, 47, 1, 1, '건축가 스케치', 'Architect sketch', 1000, 'architect', 'APPROVED', 'https://picsum.photos/seed/147/300/300', false, NOW(), NOW()),
(148, 48, 1, 2, '사진작가', 'Photographer', 900, 'photographer', 'APPROVED', 'https://picsum.photos/seed/148/300/300', false, NOW(), NOW()),
(149, 49, 1, 1, '과학자 연구', 'Scientist', 1100, 'scientist', 'APPROVED', 'https://picsum.photos/seed/149/300/300', false, NOW(), NOW()),
(150, 50, 1, 2, '무용수', 'Dancer', 1200, 'dancer', 'APPROVED', 'https://picsum.photos/seed/150/300/300', false, NOW(), NOW()),

-- 문화 (151-160)
(151, 1, 5, 1, '한국 전통 건축', 'Korean traditional', 1400, 'korean house', 'APPROVED', 'https://picsum.photos/seed/151/300/300', false, NOW(), NOW()),
(152, 2, 2, 2, '전통 풍경', 'Traditional landscape', 900, 'landscape', 'APPROVED', 'https://picsum.photos/seed/152/300/300', false, NOW(), NOW()),
(153, 3, 1, 1, '한복 인물', 'Person in hanbok', 1100, 'hanbok person', 'APPROVED', 'https://picsum.photos/seed/153/300/300', false, NOW(), NOW()),
(154, 4, 3, 2, '전통 문양', 'Traditional pattern', 700, 'pattern', 'APPROVED', 'https://picsum.photos/seed/154/300/300', false, NOW(), NOW()),
(155, 5, 5, 1, '사찰 건축', 'Temple architecture', 1300, 'temple', 'APPROVED', 'https://picsum.photos/seed/155/300/300', false, NOW(), NOW()),
(156, 6, 2, 2, '전통 정원', 'Traditional garden', 850, 'garden', 'APPROVED', 'https://picsum.photos/seed/156/300/300', false, NOW(), NOW()),
(157, 7, 3, 1, '민화 스타일', 'Minhwa style', 900, 'folk painting', 'APPROVED', 'https://picsum.photos/seed/157/300/300', false, NOW(), NOW()),
(158, 8, 1, 2, '전통 의상', 'Traditional costume', 1000, 'costume', 'APPROVED', 'https://picsum.photos/seed/158/300/300', false, NOW(), NOW()),
(159, 9, 5, 1, '궁궐 건축', 'Palace architecture', 1600, 'palace', 'APPROVED', 'https://picsum.photos/seed/159/300/300', false, NOW(), NOW()),
(160, 10, 2, 2, '전통 마을', 'Traditional village', 950, 'village', 'APPROVED', 'https://picsum.photos/seed/160/300/300', false, NOW(), NOW()),

-- 기술 (161-170)
(161, 11, 3, 1, '로봇 캐릭터', 'Robot character', 1250, 'robot', 'APPROVED', 'https://picsum.photos/seed/161/300/300', false, NOW(), NOW()),
(162, 12, 1, 2, 'AI 휴머노이드', 'AI humanoid', 1400, 'humanoid', 'APPROVED', 'https://picsum.photos/seed/162/300/300', false, NOW(), NOW()),
(163, 13, 5, 1, '미래 도시', 'Future city', 1800, 'future city', 'APPROVED', 'https://picsum.photos/seed/163/300/300', false, NOW(), NOW()),
(164, 14, 3, 2, '기술 일러스트', 'Tech illustration', 900, 'tech', 'APPROVED', 'https://picsum.photos/seed/164/300/300', false, NOW(), NOW()),
(165, 15, 1, 1, '로봇 초상', 'Robot portrait', 1100, 'robot face', 'APPROVED', 'https://picsum.photos/seed/165/300/300', false, NOW(), NOW()),
(166, 16, 4, 2, '디지털 추상', 'Digital abstract', 850, 'digital', 'APPROVED', 'https://picsum.photos/seed/166/300/300', false, NOW(), NOW()),
(167, 17, 5, 1, '첨단 건물', 'High-tech building', 1500, 'hitech', 'APPROVED', 'https://picsum.photos/seed/167/300/300', false, NOW(), NOW()),
(168, 18, 3, 2, 'AI 비서', 'AI assistant', 1000, 'ai', 'APPROVED', 'https://picsum.photos/seed/168/300/300', false, NOW(), NOW()),
(169, 19, 2, 1, '기술 풍경', 'Tech landscape', 1300, 'tech land', 'APPROVED', 'https://picsum.photos/seed/169/300/300', false, NOW(), NOW()),
(170, 20, 1, 2, '사이보그', 'Cyborg', 1600, 'cyborg', 'APPROVED', 'https://picsum.photos/seed/170/300/300', false, NOW(), NOW()),

-- 식물 (171-180)
(171, 21, 3, 1, '장미 일러스트', 'Rose illustration', 650, 'rose', 'APPROVED', 'https://picsum.photos/seed/171/300/300', false, NOW(), NOW()),
(172, 22, 2, 2, '꽃밭 풍경', 'Flower field', 800, 'flowers', 'APPROVED', 'https://picsum.photos/seed/172/300/300', false, NOW(), NOW()),
(173, 23, 3, 1, '식물 일러스트', 'Plant illustration', 550, 'plant', 'APPROVED', 'https://picsum.photos/seed/173/300/300', false, NOW(), NOW()),
(174, 24, 2, 2, '정원 풍경', 'Garden landscape', 750, 'garden', 'APPROVED', 'https://picsum.photos/seed/174/300/300', false, NOW(), NOW()),
(175, 25, 3, 1, '튤립 일러스트', 'Tulip illustration', 600, 'tulip', 'APPROVED', 'https://picsum.photos/seed/175/300/300', false, NOW(), NOW()),
(176, 26, 2, 2, '꽃 풍경', 'Flower landscape', 700, 'flower land', 'APPROVED', 'https://picsum.photos/seed/176/300/300', false, NOW(), NOW()),
(177, 27, 3, 1, '선인장 일러스트', 'Cactus illustration', 500, 'cactus', 'APPROVED', 'https://picsum.photos/seed/177/300/300', false, NOW(), NOW()),
(178, 28, 2, 2, '식물원', 'Botanical garden', 900, 'botanical', 'APPROVED', 'https://picsum.photos/seed/178/300/300', false, NOW(), NOW()),
(179, 29, 3, 1, '나무 일러스트', 'Tree illustration', 650, 'tree', 'APPROVED', 'https://picsum.photos/seed/179/300/300', false, NOW(), NOW()),
(180, 30, 2, 2, '꽃나무', 'Flowering tree', 750, 'flowering', 'APPROVED', 'https://picsum.photos/seed/180/300/300', false, NOW(), NOW()),

-- 밤 (181-190)
(181, 31, 2, 1, '밤 풍경', 'Night landscape', 900, 'night', 'APPROVED', 'https://picsum.photos/seed/181/300/300', false, NOW(), NOW()),
(182, 32, 2, 2, '달빛', 'Moonlight scene', 850, 'moonlight', 'APPROVED', 'https://picsum.photos/seed/182/300/300', false, NOW(), NOW()),
(183, 33, 2, 1, '별밤', 'Starry night', 950, 'stars', 'APPROVED', 'https://picsum.photos/seed/183/300/300', false, NOW(), NOW()),
(184, 34, 4, 2, '밤 추상', 'Night abstract', 800, 'dark abstract', 'APPROVED', 'https://picsum.photos/seed/184/300/300', false, NOW(), NOW()),
(185, 35, 2, 1, '저녁 노을', 'Evening sunset', 750, 'evening', 'APPROVED', 'https://picsum.photos/seed/185/300/300', false, NOW(), NOW()),
(186, 36, 5, 2, '야경 건물', 'Night building', 1200, 'night building', 'APPROVED', 'https://picsum.photos/seed/186/300/300', false, NOW(), NOW()),
(187, 37, 2, 1, '달과 구름', 'Moon and clouds', 700, 'moon', 'APPROVED', 'https://picsum.photos/seed/187/300/300', false, NOW(), NOW()),
(188, 38, 4, 2, '밤하늘 추상', 'Night sky abstract', 900, 'night sky', 'APPROVED', 'https://picsum.photos/seed/188/300/300', false, NOW(), NOW()),
(189, 39, 2, 1, '저녁 풍경', 'Evening landscape', 800, 'evening', 'APPROVED', 'https://picsum.photos/seed/189/300/300', false, NOW(), NOW()),
(190, 40, 1, 2, '달빛 초상', 'Moonlit portrait', 1100, 'moonlit', 'APPROVED', 'https://picsum.photos/seed/190/300/300', false, NOW(), NOW()),

-- 혼합 (191-200)
(191, 41, 3, 1, '픽셀 아트', 'Pixel art', 400, 'pixel', 'APPROVED', 'https://picsum.photos/seed/191/300/300', false, NOW(), NOW()),
(192, 42, 1, 2, '해적 선장', 'Pirate captain', 1550, 'pirate', 'APPROVED', 'https://picsum.photos/seed/192/300/300', false, NOW(), NOW()),
(193, 43, 2, 1, '화산 풍경', 'Volcano landscape', 1350, 'volcano', 'APPROVED', 'https://picsum.photos/seed/193/300/300', false, NOW(), NOW()),
(194, 44, 3, 2, '늑대 일러스트', 'Wolf illustration', 950, 'wolf', 'APPROVED', 'https://picsum.photos/seed/194/300/300', false, NOW(), NOW()),
(195, 45, 4, 1, '크리스탈 추상', 'Crystal abstract', 1250, 'crystal', 'APPROVED', 'https://picsum.photos/seed/195/300/300', false, NOW(), NOW()),
(196, 46, 5, 2, '등대 건축', 'Lighthouse architecture', 950, 'lighthouse', 'APPROVED', 'https://picsum.photos/seed/196/300/300', false, NOW(), NOW()),
(197, 47, 1, 1, '기사 초상화', 'Knight portrait', 1550, 'knight', 'APPROVED', 'https://picsum.photos/seed/197/300/300', false, NOW(), NOW()),
(198, 48, 2, 2, '대나무 숲', 'Bamboo forest', 650, 'bamboo', 'APPROVED', 'https://picsum.photos/seed/198/300/300', false, NOW(), NOW()),
(199, 49, 3, 1, '펭귄 일러스트', 'Penguin illustration', 550, 'penguin', 'APPROVED', 'https://picsum.photos/seed/199/300/300', false, NOW(), NOW()),
(200, 50, 4, 2, '입자 추상', 'Particle abstract', 850, 'particles', 'APPROVED', 'https://picsum.photos/seed/200/300/300', false, NOW(), NOW());

-- 5. Credit Charge Options (기존 동일)
INSERT IGNORE INTO credit_charge_option (id, amount) VALUES
                                                         (1, 3000),
                                                         (2, 5000),
                                                         (3, 10000),
                                                         (4, 30000),
                                                         (5, 50000);

-- 6. Bonus Credit Policies (기존 동일)
INSERT IGNORE INTO bonus_credit_policy (id, min_amount, bonus_rate, description) VALUES
                                                                                     (1, 5000, 0.05, '5% Bonus'),
                                                                                     (2, 30000, 0.10, '10% Bonus'),
                                                                                     (3, 50000, 0.15, '15% Bonus');

INSERT IGNORE INTO purchases (user_id, prompt_id, purchased_at)
VALUES (1, 1, CURRENT_TIMESTAMP);

INSERT IGNORE INTO prompt_variables (prompt_variable_id, prompt_id, key_name, variable_name, description, order_index)
VALUES (1, 1, 'color', '색상', '이미지의 주된 색상을 정합니다', 1);

-- Lookbook Images (Picsum 적용)
INSERT IGNORE INTO lookbook_images (prompt_id, image_url, is_representative, is_preview)
VALUES (1, 'https://picsum.photos/seed/lb1/300/300', true, false);

INSERT IGNORE INTO lookbook_images (prompt_id, image_url, is_representative, is_preview)
VALUES (1, 'https://picsum.photos/seed/lb2/300/300', true, true);

INSERT INTO lookbook_images (prompt_id, image_url, is_representative, is_preview)
VALUES (1, 'https://picsum.photos/seed/lb3/300/300', true, false);

INSERT INTO ai_models (name, is_active, order_index, created_at, updated_at)
VALUES ('grok-imagine/text-to-image', 1, 1, NOW(), NOW());

INSERT INTO prompt_variables (prompt_variable_id, prompt_id, key_name, variable_name, description, order_index)
VALUES (2, 2, '색상2', '색상2', '이미지의 주된 색상을 정합니다', 2);

INSERT INTO prompt_variables (prompt_variable_id, prompt_id, key_name, variable_name, description, order_index)
VALUES (3, 3, '색상3', '색상3', '이미지의 주된 색상을 정합니다', 3);
INSERT IGNORE INTO lookbook_images (prompt_id, image_url, is_representative, is_preview)
VALUES (1, 'https://picsum.photos/seed/lb3/300/300', true, false);
