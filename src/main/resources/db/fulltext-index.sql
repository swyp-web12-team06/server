-- Full-Text Search Index 추가
-- prompts 테이블의 title 컬럼에 FULLTEXT INDEX 추가
ALTER TABLE prompts ADD FULLTEXT INDEX idx_fulltext_title (title);

-- users 테이블의 nickname 컬럼에 FULLTEXT INDEX 추가
ALTER TABLE users ADD FULLTEXT INDEX idx_fulltext_nickname (nickname);

ALTER TABLE tags ADD FULLTEXT INDEX idx_fulltext_tag_name (name);

-- Full-Text Index 확인
-- SHOW INDEX FROM prompts WHERE Key_name = 'idx_fulltext_title';
-- SHOW INDEX FROM users WHERE Key_name = 'idx_fulltext_nickname';

-- Full-Text Index 삭제 (필요시)
-- ALTER TABLE prompts DROP INDEX idx_fulltext_title;
-- ALTER TABLE users DROP INDEX idx_fulltext_nickname;
-- ALTER TABLE tags DROP INDEX idx_fulltext_tag_name;
