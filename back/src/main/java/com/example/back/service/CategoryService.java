package com.example.back.service;

import com.example.back.dto.CategoryDto;
import com.example.back.dto.PageRequestDto;
import com.example.back.dto.PageResponseDto;
import com.example.back.entity.CategoryEntity;
import com.example.back.repository.CategoryRepository;
import com.example.back.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 카테고리 비즈니스 로직
 *
 * @fileName : CategoryService
 * @since : 26. 3. 24.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /*
    * 카테고리 생성
    * */
    @Transactional
    public Long register(CategoryDto.CategoryRequest request) {
        log.info("카테고리 생성 요청: {}", request);

        CategoryEntity category = request.toEntity();

        // 부모 카테고리 있을 경우 조회 및 연관관계 설정
        if (request.getParentId() != null) {
            CategoryEntity parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 카테고리가 존재하지 않습니다."));

            // 엔티티의 연관관계 편의 메섣드 사용 (양방향 세팅)
            parent.addChildCategory(category);
        }

        log.info("카테고리 생성 결과: {}", category);
        return categoryRepository.save(category).getId();
    }


    /*
     * 전체 트리 구조 조회
     * */
    public List<CategoryDto.CategoryResponse> findAllTree() {
        log.info("전체 카테고리 트리 조회");
        return categoryRepository.findRootCategories().stream()
                .map(CategoryDto.CategoryResponse::from)
                .toList();
    }

    /*
     * 카테고리 수정
     * */
    @Transactional
    public void updateCategory(Long id, CategoryDto.CategoryRequest request) {
        log.info("카테고리 수정 요청 ID: {}, Data: {}", id, request);

        // 수정할 카테고리 조회
        Optional<CategoryEntity> result = categoryRepository.findById(id);

        CategoryEntity entity = result.orElseThrow(() -> new RuntimeException("수정할 데이터를 찾을 수 없습니다. ID: " + id));

        // 비즈니스 로직 수행
        entity.update(request.getName(), request.getSortOrder());

    }


    /*
     *카테고리 삭제
     * */
    @Transactional
    public void remove(Long id) {

        log.info("카테고리 삭제 요청 ID: {}", id);

        // 삭제할 카테고리 조회
        Optional<CategoryEntity> result = categoryRepository.findById(id);

        CategoryEntity entity = result.orElseThrow(() -> new RuntimeException("삭제할 데이터를 찾을 수 없습니다. ID: " + id));

        categoryRepository.delete(entity);
        log.info("ID {}번 카테고리 삭제 완료", id);
    }

}
