package com.example.back.dto;

import com.example.back.entity.CategoryEntity;
import com.example.back.entity.ProductEntity;
import com.example.back.entity.ProductImageEntity;
import com.example.back.entity.ProductStatus;
import com.example.back.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FileName    : ProductDto
 * Since       : 26. 3. 25.
 * Dsecription  : 상품 관련 데이터 전송 객체 통합 관리
 */
public class ProductDto {

    // 등록 요청 (Create) - 상품 등록용
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class ProductCreateRequest {

        @NotBlank(message = "상품명은 필수 입력 값입니다.")
        private String title;

        @NotBlank(message = "상품 내용은 필수 입력 값입니다.")
        private String content;

        @NotNull(message = "가격은 필수 입력 값입니다.")
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        private Integer price;

        @NotNull(message = "카테고리 선택은 필수입니다.")
        private Long categoryId;

        public ProductEntity toEntity(UserEntity seller, CategoryEntity category) {

            return ProductEntity.builder()
                    .title(title)
                    .content(content)
                    .price(price)
                    .status(ProductStatus.SALE) // 기본값 판매중
                    .seller(seller)
                    .category(category)
                    .build();
        }
    }

    // 상품 수정 요청 (Update)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class ProductUpdateRequest {

        @NotBlank(message = "수정할 제목을 입력해주세요.")
        private String title;

        private String content;

        @NotNull(message = "가격을 입력해주세요.")
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        private Integer price;

        private ProductStatus status;

        private Long categoryId;
    }


    // 상품 이미지 정보 응답용 DTO
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductImageResponse {
        private Long id;
        private String originalFileName;
        private String savedFileName;
        private String imageUrl;       // 프론트가 <img src="...">에 바로 쓸 경로
        private boolean repImgYn;
        private Integer sortOrder;

        public static ProductImageResponse from(ProductImageEntity entity) {
            return ProductImageResponse.builder()
                    .id(entity.getId())
                    .originalFileName(entity.getOriginalFileName())
                    .savedFileName(entity.getSavedFileName())
                    .imageUrl("/images/" + entity.getSavedFileName())   // WebConfig에서 설정한 "/images/**" 경로와 매핑
                    .repImgYn(entity.isRepImgYn())
                    .sortOrder(entity.getSortOrder())
                    .build();
        }
    }


    // 상품 상세 조회 응답 (Read)
    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class ProductReadResponse {

        private Long id;
        private String title;
        private String content;
        private Integer price;
        private ProductStatus status;

        // 판매자 정보
        private Long sellerId;
        private String sellerNickname;

        // 카테고리 정보
        private Long categoryId;
        private String categoryName;

        // 이미지 목록 추가
        private List<ProductImageResponse> images;

        private LocalDateTime createdAt;

        public static ProductReadResponse from(ProductEntity entity) {
            return ProductReadResponse.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .price(entity.getPrice())
                    .status(entity.getStatus())
                    .sellerId(entity.getSeller().getId())
                    .sellerNickname(entity.getSeller().getNickname())
                    .categoryId(entity.getCategory().getId())
                    .categoryName(entity.getCategory().getName())
                    .images(entity.getImages().stream()
                            .map(ProductImageResponse::from)
                            .collect(java.util.stream.Collectors.toList()))
                    .createdAt(entity.getCreatedAt())
                    .build();
        }

    }


    /*
    * 상품 목록 조회
    * */
    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class ProductListResponse{
        private Long id;
        private String title;
        private Integer price;
        private String representativeImageUrl; // 대표 이미지 경로

        public static ProductListResponse from(ProductEntity entity) {
            // 대표 이미지 파일명 찾기
            String fileName = entity.getImages().stream()
                    .filter(ProductImageEntity::isRepImgYn)
                    .map(ProductImageEntity::getSavedFileName)
                    .findFirst()
                    .orElse(null);

            return ProductListResponse.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .price(entity.getPrice())
                    // 가상 경로 매핑: 파일이 없으면 null, 있으면 /images/uuid.jpg
                    .representativeImageUrl(fileName != null ? "/images/" + fileName : null)
                    .build();
        }
    }
}
