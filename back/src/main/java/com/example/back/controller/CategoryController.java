package com.example.back.controller;

import com.example.back.dto.CategoryDto;
import com.example.back.service.CategoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * FileName    : CategoryController
 * Since       : 26. 3. 24.
 * Dsecription  :
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    /*
     * 카테고리 등록
     * */
    @PostMapping
    public ResponseEntity<Long> register(@RequestBody CategoryDto.Request request) {

        log.info("REST 요청 - 등록: {}", request);

        // 등록
        Long id = categoryService.register(request);

        // 응답과 생성된 id 전달
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(id);
    }

    /*
     * 카테고리 트리 조회
     * */
    @GetMapping
    public ResponseEntity<List<CategoryDto.Response>> getAllTree() {
        log.info("REST 요청 - 전체 목록 조회 ");

        List<CategoryDto.Response> list = categoryService.findAllTree();

        log.info("목록 : {}", list);

        return ResponseEntity.ok(list);
    }

    /*
     * 카테고리 수정
     * */
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> modify(@PathVariable("id") Long id, @RequestBody CategoryDto.Request request) {
        log.info("카테고리 수정 요청 ID: {}, Data: {}", id, request);

        categoryService.updateCategory(id, request);

        return ResponseEntity.ok(Map.of("result", "success"));
    }

    /*
     * 카테고리 삭제
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remove(@PathVariable("id") Long id) {
        log.info("카테고리 삭제 요청 ID: {}", id);

        categoryService.remove(id);

        return ResponseEntity.ok(Map.of("result", "success"));
    }
}
