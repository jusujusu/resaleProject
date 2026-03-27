package com.example.back.service;

import com.example.back.dto.PageRequestDto;
import com.example.back.dto.PageResponseDto;
import com.example.back.dto.ProductDto;
import com.example.back.entity.CategoryEntity;
import com.example.back.entity.ProductEntity;
import com.example.back.entity.ProductImageEntity;
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

import java.io.IOException;
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

        // 상품 엔티티 생성
        ProductEntity entity = request.toEntity(seller, category);
        
        // 이미지 파일 처리 및 연관관계 설정
        if (files != null && !files.isEmpty()) {
            try {
                List<ProductImageEntity> images = fileUtil.storeFiles(files);
                for (int i = 0; i < images.size(); i++) {
                    ProductImageEntity image = images.get(i);

                    image.updateSortOrder(i);   // 정렬 순서 부여
                    image.updateRepImg(i == 0); // 첫번째 이미지만 대표 이미지 설정

                    // 상품 엔티티에 이미지 추가
                    entity.addImage(image);
                }
            } catch (IOException e) {
                log.error("파일 저장 중 오류 발생", e);
                throw new RuntimeException("이미지 저장 실패: " + e.getMessage());
            }
        }

        // 상품 엔티티 저장
        ProductEntity savedEntity = productRepository.save(entity);
        log.info(">>> [등록 완료] 생성된 Product ID: {}", savedEntity.getId());
        return savedEntity.getId();
    }


    /*
     * 상품 조회
     * */
    public ProductDto.ProductReadResponse getOne(Long id) {

        // 상품 조회
        Optional<ProductEntity> result = productRepository.findByIdWithDetails(id);
        //존재 하지 않을시 예외 처리
        ProductEntity entity = result.orElseThrow(() -> new RuntimeException("해당 ID의 상품을 찾을 수 없습니다. ID: " + id));

        log.info(">>> [조회 결과] Title: {}", entity.getTitle());
        return ProductDto.ProductReadResponse.from(entity);
    }

    /*
     * 상품 수정
     * */
    @Transactional
    public void modify(Long id, ProductDto.ProductUpdateRequest updateDto, List<MultipartFile> files) {

        log.info(">>> [수정 요청] Product ID: {}, New Title: {}", id, updateDto.getTitle());

        // 수정할 상품 조회
        Optional<ProductEntity> result = productRepository.findById(id);
        // 존재하지 않을 시 예외 처리
        ProductEntity entity = result.orElseThrow(() -> new RuntimeException("수정할 상품을 찾을 수 없습니다. ID: " + id));

        // 텍스트 부분 수정 비즈니스 로직 (DTO가 존재 할 경우에만)
        if (updateDto != null) {
            String newTitle = (updateDto.getTitle() != null) ? updateDto.getTitle() : entity.getTitle();
            String newContent = (updateDto.getContent() != null) ? updateDto.getTitle() : entity.getContent();
            Integer newPrice = (updateDto.getPrice() != null) ? updateDto.getPrice() : entity.getPrice();

            entity.updateProduct(newTitle, newContent, newPrice);
        }

        // 상태값(Enum)이 있으면 상태 수정
        if (updateDto.getStatus() != null) {
            entity.changeStatus(updateDto.getStatus());
        }

        // 이미지 교체 : 새 파일 리스트가 들어온 경우에만 실행
        if (files != null && !files.isEmpty()) {

            // 기존 db에 저장된 파일명들 백업
            List<String> oldFileNames = entity.getImages().stream()
                    .map(ProductImageEntity::getSavedFileName)
                    .toList();

            // 기존 연관 관계 제거
            entity.getImages().clear();

            try {
                // 새 파일 저장 및 추가
                List<ProductImageEntity> newImages = fileUtil.storeFiles(files);

                for (int i = 0; i < newImages.size(); i++) {
                    ProductImageEntity img = newImages.get(i);
                    img.updateSortOrder(i);   // 순서 부여
                    img.updateRepImg(i == 0); // 첫 번째 이미지 대표 설정
                    entity.addImage(img);
                }

                // 기존 물리 파일 삭제
                oldFileNames.forEach(fileUtil::deleteFile);

            } catch (IOException e) {
                throw new RuntimeException("이미지 수정 중 오류 발생: " + e.getMessage());
            }
        }

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
