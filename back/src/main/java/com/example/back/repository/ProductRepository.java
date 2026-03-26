package com.example.back.repository;

import com.example.back.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 상품 Repository
 *
 * @fileName : ProductRepository
 * @since : 26. 3. 23.
 */
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // 카테고리, 제목 검색쿼리
    // 기본 쿼리: 연관된 Entity를 Lazy로 가져오기 때문에 N+1 발생
//    @Query("SELECT p FROM ProductEntity p " +
//            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
//            "AND (:keyword IS NULL OR p.title LIKE %:keyword%)")
//    Page<ProductEntity> searchProducts(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable
//    );


    // 카테고리, 제목 검색 쿼리 수정버전
    // 최적화 쿼리: JOIN FETCH로 연관 데이터를 한 번에 조회 (N+1 해결)
    // 카운트 쿼리를 분리하여 페이징 성능을 추가로 높임
    @Query(value = "SELECT p FROM ProductEntity p " +
            "JOIN FETCH p.seller " +
            "JOIN FETCH p.category " +
            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:keyword IS NULL OR p.title LIKE %:keyword%)",
            countQuery = "SELECT count(p) FROM ProductEntity p " +
                    "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
                    "AND (:keyword IS NULL OR p.title LIKE %:keyword%)")
    Page<ProductEntity> searchProducts(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    /*
     * 상품 전체 목록 조회
     * 상품의 정보뿐만 아니라 판매자, 카테고리의 정보도 가져옴
     * N + 1 문제를 해결하기 위해 join 사용
     * fetch join을 사용했기 때문에 페이징 기능을 적용하려면 countQuery 필수
     * */
    @Query(value = "SELECT p FROM ProductEntity p " +
            "JOIN FETCH p.seller " +
            "JOIN FETCH p.category",
            countQuery = "SELECT count(p) FROM ProductEntity p")
    Page<ProductEntity> findAllWithFetch(Pageable pageable);

    /*
     * 삭제된 데이터 포함 단건 조회 (물리 삭제용)
     */
    @Query(value = "SELECT * FROM products WHERE id = :id", nativeQuery = true)
    Optional<ProductEntity> findByIdIncludeDeleted(@Param("id") Long id);

}
