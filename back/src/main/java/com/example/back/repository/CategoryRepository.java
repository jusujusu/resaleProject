package com.example.back.repository;

import com.example.back.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 카테고리 Repository
 *
 * @fileName : CategoryRepository
 * @since : 26. 3. 23.
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    /*
    * 최상위 카테고리들만 순서대로 조회
    * */
    @Query("SELECT c FROM CategoryEntity c WHERE c.parent IS NULL ORDER BY c.sortOrder ASC")
    List<CategoryEntity> findRootCategories();

    /*
    * 특정 부모의 자식 카테고리 조회
    * */
    @Query("SELECT c FROM CategoryEntity c WHERE c.parent.id = :parentId ORDER BY c.sortOrder ASC")
    List<CategoryEntity> findChildrenByParentId(@Param("parentId") Long parentId);

    /*
    * 카테고리에 연결된 상품이 있는지 확인
    * */
    @Query("SELECT COUNT(p) > 0 FROM ProductEntity p WHERE p.category.id = :categoryId")
    boolean hasProducts(@Param("categoryId") Long categoryId);
}
