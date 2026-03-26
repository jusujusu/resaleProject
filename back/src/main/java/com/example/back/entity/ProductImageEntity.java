package com.example.back.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * FileName    : ProductImageEntity
 * Since       : 26. 3. 26.
 * Dsecription  : 상품의 이미지 테이블 설정
 */

@Entity
@Table(name = "product_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImageEntity extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 식별자 (PK)

    private String originalFileName;    // 원본 파일명

    private String savedFileName;       // 서버에 저장된 파일명

    private boolean repImgYn;           // 대표 이미지 여부

    // 상품
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;


    // -------------- 빌더 패턴 --------------
    @Builder
    public ProductImageEntity(String originalFileName, String savedFileName, boolean repImgYn) {
        this.originalFileName = originalFileName;
        this.savedFileName = savedFileName;
        this.repImgYn = repImgYn;
    }


    // 연관관계 편의 메서드
    public void setProduct(ProductEntity product) {
        this.product = product;
    }

}
