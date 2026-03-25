package com.example.back.repository;

import com.example.back.entity.CategoryEntity;
import com.example.back.entity.ProductEntity;
import com.example.back.entity.ProductStatus;
import com.example.back.user.entity.UserEntity;
import com.example.back.user.entity.UserRole;
import com.example.back.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProductRepositoryTest
 *
 * @fileName : ProductRepositoryTest
 * @since : 26. 3. 23.
 */

@SpringBootTest
@Slf4j
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private UserEntity seller;
    private CategoryEntity category;


    @BeforeEach
    void setUp() {
        String testEmail = "fixed_seller@test.com";

        // 1. 해당 이메일의 유저가 있는지 확인
        seller = userRepository.findByEmail(testEmail)
                .orElseGet(() -> userRepository.save(UserEntity.builder()
                        .email(testEmail)
                        .password("1234")
                        .phoneNumber("010-0000-0000")
                        .nickname("고정판매자")
                        .address("부천시")
                        .detailAddress("오정구")
                        .role(UserRole.USER)
                        .build()));

        //  카테고리
        category = categoryRepository.findById(9L)
                .orElseThrow(() -> new RuntimeException("DB에 ID 9번 카테고리가 없습니다. 먼저 카테고리를 생성해주세요."));

        log.info("사용 중인 카테고리: {}", category.getName());
    }

    @Test
    @DisplayName("상품 생성 테스트")
    void createTest() {
        log.info("========== 상품 생성 테스트 시작 ==========");


        ProductEntity product = ProductEntity.builder()
                .title("아이폰")
                .content("상태 좋음!!!!")
                .price(250)
                .seller(seller)
                .category(category)
                .build();

        ProductEntity savedProduct = productRepository.save(product);

        log.info("등록된 상품 ID :{}, 제목: {}", savedProduct.getId(), savedProduct.getTitle());
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getSeller().getNickname()).isEqualTo("고정판매자");

    }

    @Test
    @DisplayName("상품 10개 연속 생성 테스트")
    void create10product() {
        log.info("========== 상품 10개 생성 시작 ==========");

        // 10개의 상품 객체 생성 (Stream 활용)
        List<ProductEntity> products = IntStream.range(0, 10)
                .mapToObj(i -> ProductEntity.builder()
                        .title("중고 물품 판매 " + i)
                        .content("이 상품은 테스트용 상품 " + i + " 번입니다. 상태 아주 좋아요!")
                        .price(10000 + (i * 500))
                        .seller(seller)
                        .category(category)
                        .build())
                .toList();

        // list를 한번에 저장 (saveAll)
        List<ProductEntity> savedProducts = productRepository.saveAll(products);


        // 결과 로그
        log.info("저장된 상품 수: {}", savedProducts.size());
        savedProducts.forEach(product ->
                log.info("ID: {}, 제목: {}, 가격: {}, 판매자: {}, 카테고리: {}",
                        product.getId(),
                        product.getTitle(),
                        product.getPrice(),
                        product.getSeller().getNickname(),
                        product.getCategory().getName()));


        // 검증
        assertThat(savedProducts).hasSize(10);
        assertThat(savedProducts).allMatch(p -> p.getId() != null);
        assertThat(savedProducts).allMatch(p -> p.getPrice() >= 10000);

        log.info("========== 상품 10개 생성 테스트 종료 ==========");
    }


    @Test
    @DisplayName("전체 목록 조회 테스트")
    void findAllTest() {
        log.info("========== 목록 조회 시작 ==========");

        // 전체 조회
        List<ProductEntity> productEntityList = productRepository.findAll();

        // 검증
        assertThat(productEntityList).isNotEmpty();


        log.info("현재 전체 상품 수 : {}", productEntityList.size());
        productEntityList.stream().forEach(product ->
                log.info("최신 상품 - 제목: {}, 등록일: {}", product.getTitle(), product.getCreatedAt())
        );
    }


    @Test
    @DisplayName("상품 정보 수정 테스트")
    void updateTest() {
        log.info("========== 상품 수정 테스트 시작 ==========");

        // 수정할 상품 생성
        ProductEntity product = productRepository.save(
                ProductEntity.builder()
                        .title("수정 전 상품명").
                        price(1000).
                        status(ProductStatus.SALE).
                        seller(seller).
                        category(category)
                        .build()
        );

        // 생성한 상품 수정
        product.updateProduct(
                "이름 수정",
                "내용 수정",
                880
        );
        product.changeStatus(ProductStatus.SOLD);


        productRepository.saveAndFlush(product);

        // 검증
        ProductEntity updated = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("이름 수정");
        assertThat(updated.getStatus()).isEqualTo(ProductStatus.SOLD);

        log.info("수정 성공 - 제목: {}, 상태: {}", updated.getTitle(), updated.getStatus());

    }

    @Test
    @DisplayName("상품 삭제 테스트")
    void deleteProduct() {
        log.info("========== 상품 삭제 테스트 시작 ==========");

        // 기존 데이터 중 첫 번째 상품 조회
        ProductEntity product = productRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("상품 데이터가 없습니다."));


        log.info("삭제할 상품 이름 : {}", product.getTitle());

        // 삭제
        productRepository.delete(product);
    }





}
