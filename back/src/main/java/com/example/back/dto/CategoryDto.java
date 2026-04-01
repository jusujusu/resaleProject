package com.example.back.dto;

import com.example.back.entity.CategoryEntity;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Category 관련 데이터 전송 객체 통합 관리
 * 용도별로 내부 클래스를 분리하여 관리 포인트 최적화
 */
public class CategoryDto {

    // 요청 dto - 등록, 수정에 사용
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class CategoryRequest {
        private String name;
        
        private Long parentId;  // 부모 카테고리 ID (선택사항)
        
        private Integer sortOrder;  // 정렬 순서 (선택사항)

        public CategoryEntity toEntity() {
            return CategoryEntity.builder()
                    .name(name)
                    .sortOrder(sortOrder != null ? sortOrder : 0)
                    .build();
        }
    }



    // 목록 계층 조회용
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class CategoryResponse {
        private Long id;
        private String name;
        private Integer sortOrder;
        private List<CategoryResponse> children;

        public static CategoryResponse from(CategoryEntity entity) {
            return CategoryResponse.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .sortOrder(entity.getSortOrder())
                    // 자식이 없으면 빈 리스트, 있으면 재귀적으로 변환
                    .children(entity.getChildren() == null ? List.of() :
                            entity.getChildren().stream()
                                    .map(CategoryResponse::from)
                                    .collect(Collectors.toList()))
                    .build();
        }
    }



}
