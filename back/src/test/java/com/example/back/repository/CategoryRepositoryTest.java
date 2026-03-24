package com.example.back.repository;

import com.example.back.entity.CategoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import lombok.extern.slf4j.Slf4j;

/**
 * CategoryRepositoryTest
 *
 * @fileName : ProductRepositoryTest
 * @since : 26. 3. 23.
 */

@SpringBootTest
@Slf4j
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 생성 테스트")
    void createTest() {
        log.info("========== 카테고리 생성 테스트 시작 ==========");

        // 최상위 카테고리 생성
        CategoryEntity rootCategory = CategoryEntity.builder()
                .name("전자기기")
                .sortOrder(1)
                .build();

        CategoryEntity savedRootCategory = categoryRepository.save(rootCategory);
        log.info("저장된 최상위 카테고리: ID={}, Name={}", 
            savedRootCategory.getId(), savedRootCategory.getName());

        // 자식 카테고리 생성
        CategoryEntity childCategory1 = CategoryEntity.builder()
                .name("디지털기기")
                .parent(savedRootCategory)
                .sortOrder(1)
                .build();

        CategoryEntity childCategory2 = CategoryEntity.builder()
                .name("가전기기")
                .parent(savedRootCategory)
                .sortOrder(2)
                .build();

        CategoryEntity savedChild1 = categoryRepository.save(childCategory1);
        CategoryEntity savedChild2 = categoryRepository.save(childCategory2);

        log.info("저장된 자식 카테고리1: ID={}, Name={}", 
            savedChild1.getId(), savedChild1.getName());
        log.info("저장된 자식 카테고리2: ID={}, Name={}", 
            savedChild2.getId(), savedChild2.getName());

        assertThat(savedRootCategory.getId()).isNotNull();
        assertThat(savedChild1.getId()).isNotNull();
        assertThat(savedChild2.getId()).isNotNull();
        assertThat(savedChild1.getParent().getId()).isEqualTo(savedRootCategory.getId());
        assertThat(savedChild2.getParent().getId()).isEqualTo(savedRootCategory.getId());

        log.info("========== 카테고리 생성 테스트 종료 ==========");
    }

    @Test
    @DisplayName("최상위 카테고리 조회 테스트")
    void findRootCategoriesTest() {
        log.info("========== 최상위 카테고리 조회 테스트 시작 ==========");

        List<CategoryEntity> rootCategories = categoryRepository.findRootCategories();
        
        log.info("최상위 카테고리 수: {}", rootCategories.size());
        
        rootCategories.forEach(category -> 
            log.info("최상위 카테고리: ID={}, Name={}, SortOrder={}", 
                category.getId(), category.getName(), category.getSortOrder()));

        assertThat(rootCategories).isNotEmpty();
        
        // 모든 최상위 카테고리는 부모가 없어야 함
        rootCategories.forEach(category -> 
            assertThat(category.getParent()).isNull());

        log.info("========== 최상위 카테고리 조회 테스트 종료 ==========");
    }

    @Test
    @DisplayName("자식 카테고리 조회 테스트")
    void findChildrenByParentIdTest() {
        log.info("========== 자식 카테고리 조회 테스트 시작 ==========");

        // 기존 최상위 카테고리 중 하나를 찾음
        CategoryEntity parentCategory = categoryRepository.findRootCategories().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("최상위 카테고리가 없습니다."));

        log.info("부모 카테고리: ID={}, Name={}", 
            parentCategory.getId(), parentCategory.getName());

        // 해당 부모의 자식 카테고리 조회
        List<CategoryEntity> children = categoryRepository.findChildrenByParentId(parentCategory.getId());
        
        log.info("자식 카테고리 수: {}", children.size());
        
        children.forEach(child -> 
            log.info("자식 카테고리: ID={}, Name={}, SortOrder={}", 
                child.getId(), child.getName(), child.getSortOrder()));

        // 모든 자식 카테고리의 부모가 조회된 부모와 일치해야 함
        children.forEach(child -> 
            assertThat(child.getParent().getId()).isEqualTo(parentCategory.getId()));

        log.info("========== 자식 카테고리 조회 테스트 종료 ==========");
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    void updateTest() {
        log.info("========== 카테고리 수정 테스트 시작 ==========");

        // 기존 카테고리 중 하나를 찾음
        CategoryEntity category = categoryRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("카테고리가 없습니다."));

        log.info("수정 전 카테고리: ID={}, Name={}, SortOrder={}", 
            category.getId(), category.getName(), category.getSortOrder());

        // 카테고리 정보 수정
        category.update("수정된 카테고리명", 999);
        CategoryEntity updatedCategory = categoryRepository.save(category);

        // 수정된 정보 확인
        CategoryEntity foundCategory = categoryRepository.findById(updatedCategory.getId()).orElse(null);
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getName()).isEqualTo("수정된 카테고리명");
        assertThat(foundCategory.getSortOrder()).isEqualTo(999);

        log.info("수정된 카테고리: Name={}, SortOrder={}", 
            foundCategory.getName(), foundCategory.getSortOrder());
        log.info("========== 카테고리 수정 테스트 종료 ==========");
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    void deleteTest() {
        log.info("========== 카테고리 삭제 테스트 시작 ==========");

        // 기존 카테고리 중 하나를 찾음
        CategoryEntity category = categoryRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("카테고리가 없습니다."));

        Long categoryId = category.getId();
        log.info("삭제할 카테고리: ID={}, Name={}", categoryId, category.getName());

        // 삭제 전 확인
        assertThat(categoryRepository.findById(categoryId)).isPresent();

        // 삭제
        categoryRepository.delete(category);

        // 삭제 후 확인
        assertThat(categoryRepository.findById(categoryId)).isEmpty();

        log.info("삭제된 카테고리 ID: {}", categoryId);
        log.info("========== 카테고리 삭제 테스트 종료 ==========");
    }

    @Test
    @DisplayName("카테고리 목록 조회 테스트")
    void readListTest() {
        log.info("========== 카테고리 목록 조회 테스트 시작 ==========");

        // 전체 카테고리 목록 조회
        List<CategoryEntity> allCategories = categoryRepository.findAll();
        
        log.info("전체 카테고리 수: {}", allCategories.size());
        
        // 목록이 비어있지 않은지 확인
        assertThat(allCategories).isNotEmpty();
        
        // 각 카테고리의 기본 정보 출력
        allCategories.forEach(category -> {
            String parentName = category.getParent() != null ? category.getParent().getName() : "최상위";
            log.info("카테고리 정보 - ID: {}, Name: {}, Parent: {}, SortOrder: {}", 
                category.getId(), category.getName(), parentName, category.getSortOrder());
        });

        // 최상위 카테고리 수 확인
        long rootCount = allCategories.stream()
                .filter(category -> category.getParent() == null)
                .count();
        
        log.info("최상위 카테고리 수: {}", rootCount);
        log.info("하위 카테고리 수: {}", allCategories.size() - rootCount);

        log.info("========== 카테고리 목록 조회 테스트 종료 ==========");
    }
}
