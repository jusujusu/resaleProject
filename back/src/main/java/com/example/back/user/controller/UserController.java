package com.example.back.user.controller;

import com.example.back.dto.PageRequestDto;import com.example.back.dto.PageResponseDto;import com.example.back.user.dto.UserDto;
import com.example.back.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * FileName    : UserController
 * Since       : 26. 3. 24.
 * Dsecription  :
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    /*
     * 사용자 등록
     * */
    @PostMapping
    public ResponseEntity<Long> register(@RequestBody UserDto.CreateRequest request) {

        log.info("REST 요청 - 등록: {}", request);

        // 등록
        Long id = userService.register(request);
        log.info("등록된 id 값: {}", id);

        // 응답과 생성된 id 전달
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(id);
    }


    /*
     * 사용자 조회
     * */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.ReadResponse> getOne(@PathVariable("id") Long id) {

        log.info("REST 요청 - 상세 조회 ID: {}", id);

        UserDto.ReadResponse response = userService.getOneById(id);
        log.info("조회 정보: {}", response);

        return ResponseEntity.ok(response);
    }

    /*
    * 페이징 없는 목록 조회
    * */
    @GetMapping("/all")
    public ResponseEntity<List<UserDto.ListResponse>> getAllList() {
        log.info("REST 요청 - 전체 목록 조회");
        List<UserDto.ListResponse> list = userService.getAllList();
        return ResponseEntity.ok(list);
    }


    /*
     * 페이징 목록 조회
     * */
    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<UserDto.ListResponse>> getList(PageRequestDto pageRequestDto) {

        log.info("REST 요청 - 페이징 조회: page={}, size={}", 
                pageRequestDto.getPage(), pageRequestDto.getSize());

        PageResponseDto<UserDto.ListResponse> response = userService.getListPage(pageRequestDto);
        log.info("페이징 목록 : {}", response);

        return ResponseEntity.ok(response);
    }


    /*
    * 수정
    * */
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> modify(
            @PathVariable("id") Long id,
            @RequestBody UserDto.UpdateRequest updateDto) {
        log.info("REST 요청 - 수정 ID: {}, 데이터: {}", id, updateDto);
        userService.modify(id, updateDto);

        return ResponseEntity.ok(Map.of("result", "success"));
    }


    /*
    * 삭제
    * */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remove(@PathVariable("id") Long id) {
        log.info("REST 요청 - 삭제 ID: {}", id);
        userService.remove(id);

        return ResponseEntity.ok(Map.of("result", "success"));
    }

}
