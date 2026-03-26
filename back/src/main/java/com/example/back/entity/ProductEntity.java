package com.example.back.entity;

import com.example.back.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

/**
 * 상품 엔티티
 *
 * @fileName : ProductEntity
 * @since : 26. 3. 23.
 */

@Entity
@Table(name = "products")
@Getter
@ToString(exclude = {"seller", "category"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false") // 생성시 삭제 기본값 설정 (생성하는 쿼리뒤에 AND is_deleted = false 자동 생성 )
public class ProductEntity extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 식별자 (PK)

    @Column(nullable = false, length = 100)
    private String title; // 상품 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 상품 상세 설명

    @Column(nullable = false)
    private Integer price; // 판매 가격

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status; // 판매 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // 판매자가 삭제되면 자동 삭제
    private UserEntity seller; // 판매자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category; // 상품 카테고리

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImageEntity> images = new ArrayList<>();    // 상품 이미지

    // -------------- 빌더 패턴 --------------
    @Builder
    public ProductEntity(String title, String content, Integer price, ProductStatus status, UserEntity seller, CategoryEntity category) {

        this.title = title;
        this.content = content;
        this.price = price;
        this.status = (status != null) ? status : ProductStatus.SALE;   // 기본은 SALE
        this.seller = seller;
        this.category = category;
    }

    // -------------- 비즈니스 로직 --------------
    /*
     * 상태 변경 메소드
     * */
    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    /*
     * 상품 수정 메소드
     * */
    public void updateProduct(String title, String content, Integer price) {
        this.title = title;
        this.content = content;
        this.price = price;
    }

    /*
     * 상품 이미지 추가
     * */
    public void addImage(ProductImageEntity image) {
        this.images.add(image);
        image.setProduct(this); // 상품 이미지 엔티티에서 생서한 연관관계 편의 메서드
    }
}
