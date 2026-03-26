package com.example.back.service;

import com.example.back.dto.PageRequestDto;
import com.example.back.dto.PageResponseDto;
import com.example.back.dto.ProductDto;
import com.example.back.entity.CategoryEntity;
import com.example.back.entity.ProductEntity;
import com.example.back.repository.CategoryRepository;
import com.example.back.repository.ProductImageRepository;
import com.example.back.repository.ProductRepository;
import com.example.back.util.FileUtil;
import com.example.back.user.entity.UserEntity;
import com.example.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FileName    : ProductService
 * Since       : 26. 3. 25.
 * Dsecription  :
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductImageRepository productImageRepository;
    private final FileUtil fileUtil;


    /*
     * 상품 등록
     * */
    @Transactional
    public Long registerProduct(ProductDto.ProductCreateRequest request, List<MultipartFile> files, String email) {
        log.info("상품 등록 시도 - 작성자 이메일: {}, 상품명: {}", email, request.getTitle());

        // 판매자 조회 (추후 로그인한 사용자 값 가져오기로 수정)
        Optional<UserEntity> userResult = userRepository.findByEmail(email);
        // 존재하지 않을 시 예외 처리
        UserEntity seller = userResult.orElseThrow(() -> new RuntimeException("해당 이메일을 가진 사용자를 찾을 수 없습니다. Email: " + email));
        log.info(">>> [판매자 확인] Nickname: {}", seller.getNickname());


        // 카테고리 조회
        Optional<CategoryEntity> categoryResult = categoryRepository.findById(request.getCategoryId());
        // 존재하지 않을 시 예외 처리
        CategoryEntity category = categoryResult.orElseThrow(() -> new RuntimeException("해당 ID의 카테고리를 찾을 수 없습니다. ID: " + request.getCategoryId()));
        log.info(">>> [카테고리 확인] Category Name: {}", category.getName());

        // 1. 상품 엔티티 생성 및 저장
        ProductEntity entity = request.toEntity(seller, category);
        
        // 2. 이미지 파일 처리 및 연관관계 설정
        if (files != null && !files.isEmpty()) {
            try {
                List<com.example.back.entity.ProductImageEntity> images = fileUtil.storeFiles(files);
                for (com.example.back.entity.ProductImageEntity image : images) {
                    entity.addImage(image);
                }
            } catch (java.io.IOException e) {
                log.error("파일 저장 중 오류 발생", e);
                throw new RuntimeException("이미지 저장 실패: " + e.getMessage());
            }
        }

        ProductEntity savedEntity = productRepository.save(entity);
        log.info(">>> [등록 완료] 생성된 Product ID: {}", savedEntity.getId());
        return savedEntity.getId();
    }


    /*
     * 상품 조회
     * */
    public ProductDto.ProductReadResponse getOne(Long id) {

        Optional<ProductEntity> result = productRepository.findById(id);
        //존재 하지 않을시 예외 처리
        ProductEntity entity = result.orElseThrow(() -> new RuntimeException("해당 ID의 상품을 찾을 수 없습니다. ID: " + id));

        log.info(">>> [조회 결과] Title: {}", entity.getTitle());
        return ProductDto.ProductReadResponse.from(entity);
    }

    /*
     * 상품 수정
     * */
    @Transactional
    public void modify(Long id, ProductDto.ProductUpdateRequest updateDto) {

        log.info(">>> [수정 요청] Product ID: {}, New Title: {}", id, updateDto.getTitle());

        // 수정할 상품 조회
        Optional<ProductEntity> result = productRepository.findById(id);
        // 존재하지 않을 시 예외 처리
        ProductEntity entity = result.orElseThrow(() -> new RuntimeException("수정할 상품을 찾을 수 없습니다. ID: " + id));

        // 비즈니스 로직 실행
        entity.updateProduct(
                updateDto.getTitle(),
                updateDto.getContent(),
                updateDto.getPrice()
        );

        entity.changeStatus(
                updateDto.getStatus()
        );

        log.info(">>> [수정 완료] Product ID: {}", id);
    }


    /*
     *  [논리 삭제] 삭제 여부 플래그만 변경 (일반 회원용)
     * */
    @Transactional
    public void removeSoft(Long id) {
        log.info("논리 삭제 요청 ID: {}", id);
        Optional<ProductEntity> result = productRepository.findById(id);
        ProductEntity entity = result.orElseThrow(() -> new RuntimeException("삭제할 상품을 찾을 수 없습니다. ID: " + id));

        // BaseTimeEntity의 메소드 호출
        entity.softDelete();

        log.info("ID {}번 상품 비활성화(is_deleted=true) 완료", id);
    }

    /*
     * [물리 삭제] 데이터 실제 삭제 (관리자용)
     * */
    @Transactional
    public void remove(Long id) {
        log.info("물리 삭제 요청 ID: {}", id);
        Optional<ProductEntity> result = productRepository.findByIdIncludeDeleted(id);
        ProductEntity entity = result.orElseThrow(() -> new RuntimeException("삭제할 상품을 찾을 수 없습니다. ID: " + id));
        productRepository.delete(entity);
        log.info("ID {}번 데이터 삭제 완료", id);
    }


    /*
     * 상품 목록 (검색x)
     * */
    public PageResponseDto<ProductDto.ProductListResponse> getListPage(PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable("id");
        Page<ProductEntity> result = productRepository.findAllWithFetch(pageable);
        return convertToPageResponse(result);
    }



    /*
     * 상품 목록 검색
     * 상품 이름, 카테고리를 검색 (null 가능)
     * */
    public PageResponseDto<ProductDto.ProductListResponse> getSearchList(Long categoryId, String keyword, Pageable pageable) {
        log.info(">>> [목록 검색 요청] CategoryID: {}, Keyword: {}, Page: {}", categoryId, keyword, pageable.getPageNumber());

        return null;
    }


    /*
     * [공통 로직] Page 결과를 공통 응답 DTO로 변환
     * */
    private PageResponseDto<ProductDto.ProductListResponse> convertToPageResponse(Page<ProductEntity> result) {
        List<ProductDto.ProductListResponse> dtoList = result.getContent().stream()
                .map(ProductDto.ProductListResponse::from)
                .collect(Collectors.toList());
        return new PageResponseDto<>(result, dtoList);
    }

}
