package com.example.back.controller;

import com.example.back.dto.CategoryDto;
import com.example.back.dto.ProductDto;
import com.example.back.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * 실제 서비스에서는 SecurityContextHolder에서 email을 추출하지만,
     * 현재는 테스트를 위해 Header나 RequestParam으로 받는다고 가정합니다.
     * */
    @PostMapping
    public ResponseEntity<Long> register(@Valid @RequestBody ProductDto.ProductCreateRequest request, @RequestParam String email) {
        log.info("REST 요청 - 사용자: {} 상품: {}", email, request);

        // 등록
        Long id = productService.registerProduct(request, email);

        // 응답과 생성된 id 전달
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(id);
    }

    /*
     * 상품 조회
     * */
    @GetMapping({"/{id}"})
    public ResponseEntity<ProductDto.ProductReadResponse> getOne(@PathVariable("id") Long id) {

        log.info("REST 요청 - 상세 조회 ID: {}", id);

        ProductDto.ProductReadResponse response = productService.getOne(id);
        log.info("조회 정보: {}", response);

        return ResponseEntity.ok(response);
    }

    /*
    * 상품 정보 수정
    * */
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> modify(@PathVariable("id") Long id, @RequestBody ProductDto.ProductUpdateRequest updateDto) {

        log.info("REST 요청 - 수정 ID: {}, 데이터: {}", id, updateDto);
        productService.modify(id, updateDto);

        return ResponseEntity.ok(Map.of("result", "success"));
    }

    /*
    * 논리적 삭제
    * */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remove(@PathVariable("id") Long id) {
        log.info("REST 요청 - 상품 삭제(Soft Delete) ID: {}", id);

        // 삭제 상태값만 변경
        productService.removeSoft(id);

        return ResponseEntity.ok(Map.of("result", "success", "message", "상품 삭제가 완료되었습니다."));
    }


    /*
     * 물리적 삭제
     * */
    @DeleteMapping("/admin/hard/{id}")
    public ResponseEntity<Map<String, String>> removeHard(@PathVariable("id") Long id) {
        log.info("REST 요청 - 삭제 ID: {}", id);
        productService.remove(id);

        return ResponseEntity.ok(Map.of("result", "success"));
    }

}
