package com.example.back.controller;

import com.example.back.dto.PageRequestDto;
import com.example.back.dto.PageResponseDto;
import com.example.back.dto.ProductDto;
import com.example.back.service.ProductService;
import com.example.back.user.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * FileName    : ProductController
 * Since       : 26. 3. 25.
 * Dsecription  :
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;


    /*
     * 상품 등록
     * */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Long> register(
            @Valid @RequestPart("productDto") ProductDto.ProductCreateRequest request,
            @RequestPart(value = "files", required = false) java.util.List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("REST 요청 - 상품 등록 - 이메일: {}, 데이터: {}, 파일 개수: {}",
                userDetails.getUsername(), request, (files != null ? files.size() : 0));

        // 등록
        Long id = productService.registerProduct(request, files, userDetails.getUsername());

        // 응답과 생성된 id 전달
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(id);
    }

    /*
     * 상품 조회
     * */
    @GetMapping({"/{id}"})
    public ResponseEntity<ProductDto.ProductReadResponse> getOne(
            @PathVariable("id") Long id) {

        log.info("REST 요청 - 상세 조회 ID: {}", id);

        ProductDto.ProductReadResponse response = productService.getOne(id);
        log.info("조회 정보: {}", response);

        return ResponseEntity.ok(response);
    }


    /*
     * 상품 정보 수정
     * */
    @PatchMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> modify(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "productDto", required = false) ProductDto.ProductUpdateRequest updateDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        log.info("REST 요청 - 수정 ID: {}, 데이터: {}", id, updateDto);
        productService.modify(id, userDetails.getId(), userDetails.getRole(), updateDto, files);

        return ResponseEntity.ok(Map.of("result", "success"));
    }

    /*
     * 논리적 삭제
     * */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> remove(
            @PathVariable("id") Long id) {
        log.info("REST 요청 - 상품 삭제(Soft Delete) ID: {}", id);

        // 삭제 상태값만 변경
        productService.removeSoft(id);

        return ResponseEntity.ok(Map.of("result", "success", "message", "상품 삭제가 완료되었습니다."));
    }


    /*
     * 물리적 삭제
     * */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, String>> removeHard(
            @PathVariable("id") Long id) {
        log.info("REST 요청 - 삭제 ID: {}", id);
        productService.remove(id);

        return ResponseEntity.ok(Map.of("result", "success"));
    }


    /*
     * 상품 목록 (검색 x)
     * */
    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<ProductDto.ProductListResponse>> list(
            PageRequestDto pageRequestDto) {

        log.info(">>> [목록 조회 요청] Page: {}, Size: {}", pageRequestDto.getPage(), pageRequestDto.getSize());

        PageResponseDto<ProductDto.ProductListResponse> response = productService.getListPage(pageRequestDto);

        return ResponseEntity.ok(response);
    }

}
