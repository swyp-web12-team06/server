package com.redot.repository;

import com.redot.domain.Prompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findBySeller_IdOrderByCreatedAtDesc(Long sellerId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Prompt p SET p.isDeleted = true WHERE p.seller.id = :userId")
    void softDeleteAllByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Prompt p SET p.isDeleted = true WHERE p.id = :promptId")
    void softDeleteById(@Param("promptId") Long promptId);

    @Query("SELECT DISTINCT p FROM Prompt p " +
           "JOIN FETCH p.seller " +
           "JOIN FETCH p.category " +
           "JOIN FETCH p.aiModel " +
           "LEFT JOIN FETCH p.tags " +
           "WHERE p.id = :id AND p.isDeleted = false")
    Optional<Prompt> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Prompt p " +
           "JOIN FETCH p.seller " +
           "JOIN FETCH p.category " +
           "JOIN FETCH p.aiModel " +
           "LEFT JOIN FETCH p.tags " +
           "WHERE p.isDeleted = false")
    Page<Prompt> findAllWithDetails(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Prompt p " +
           "JOIN FETCH p.seller " +
           "JOIN FETCH p.category " +
           "JOIN FETCH p.aiModel " +
           "LEFT JOIN FETCH p.tags " +
           "WHERE p.isDeleted = false AND p.category.id = :categoryId")
    Page<Prompt> findAllByCategoryWithDetails(@Param("categoryId") Long categoryId, Pageable pageable);

    // 로컬(H2)용: JPQL LIKE 검색
    @Query("SELECT DISTINCT p FROM Prompt p " +
            "JOIN FETCH p.seller u " +
            "JOIN FETCH p.category " +
            "JOIN FETCH p.aiModel " +
            "LEFT JOIN FETCH p.tags t " +
            "WHERE p.isDeleted = false " +
            "AND (p.title LIKE %:keyword% OR u.nickname LIKE %:keyword% OR t.name LIKE %:keyword%)")
    Page<Prompt> searchByKeywordBasic(@Param("keyword") String keyword, Pageable pageable);

    // Full-Text Search 사용 (MySQL 전용)
    @Query(value = "SELECT DISTINCT p.* FROM prompts p " +
           "INNER JOIN users u ON p.user_id = u.user_id " +
           "LEFT JOIN prompt_tags pt ON p.prompt_id = pt.prompt_id " +
           "LEFT JOIN tags t ON pt.tag_id = t.tag_id " +
           "WHERE p.is_deleted = false " +
           "AND (MATCH(p.title) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
           "OR MATCH(u.nickname) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
           "OR MATCH(t.name) AGAINST(:keyword IN NATURAL LANGUAGE MODE))",
           countQuery = "SELECT COUNT(DISTINCT p.prompt_id) FROM prompts p " +
                       "INNER JOIN users u ON p.user_id = u.user_id " +
                       "LEFT JOIN prompt_tags pt ON p.prompt_id = pt.prompt_id " +
                       "LEFT JOIN tags t ON pt.tag_id = t.tag_id " +
                       "WHERE p.is_deleted = false " +
                       "AND (MATCH(p.title) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
                       "OR MATCH(u.nickname) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
                       "OR MATCH(t.name) AGAINST(:keyword IN NATURAL LANGUAGE MODE))",
           nativeQuery = true)
    Page<Prompt> searchByKeywordFullText(@Param("keyword") String keyword, Pageable pageable);

    // 로컬(H2)용: JPQL LIKE 검색 + 카테고리 필터
    @Query("SELECT DISTINCT p FROM Prompt p " +
            "JOIN FETCH p.seller u " +
            "JOIN FETCH p.category " +
            "JOIN FETCH p.aiModel " +
            "LEFT JOIN FETCH p.tags t " +
            "WHERE p.isDeleted = false " +
            "AND p.category.id = :categoryId " +
            "AND (p.title LIKE %:keyword% OR u.nickname LIKE %:keyword% OR t.name LIKE %:keyword%)")
    Page<Prompt> searchByKeywordAndCategoryBasic(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);

    // Full-Text Search 사용 (MySQL 전용)
    @Query(value = "SELECT DISTINCT p.* FROM prompts p " +
           "INNER JOIN users u ON p.user_id = u.user_id " +
           "LEFT JOIN prompt_tags pt ON p.prompt_id = pt.prompt_id " +
           "LEFT JOIN tags t ON pt.tag_id = t.tag_id " +
           "WHERE p.is_deleted = false " +
           "AND p.category_id = :categoryId " +
           "AND (MATCH(p.title) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
           "OR MATCH(u.nickname) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
           "OR MATCH(t.name) AGAINST(:keyword IN NATURAL LANGUAGE MODE))",
           countQuery = "SELECT COUNT(DISTINCT p.prompt_id) FROM prompts p " +
                       "INNER JOIN users u ON p.user_id = u.user_id " +
                       "LEFT JOIN prompt_tags pt ON p.prompt_id = pt.prompt_id " +
                       "LEFT JOIN tags t ON pt.tag_id = t.tag_id " +
                       "WHERE p.is_deleted = false " +
                       "AND p.category_id = :categoryId " +
                       "AND (MATCH(p.title) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
                       "OR MATCH(u.nickname) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
                       "OR MATCH(t.name) AGAINST(:keyword IN NATURAL LANGUAGE MODE))",
           nativeQuery = true)
    Page<Prompt> searchByKeywordAndCategoryFullText(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
}