package com.example.back.user.controller;

import com.example.back.dto.PageRequestDto;import com.example.back.dto.PageResponseDto;import com.example.back.user.dto.UserDto;
import com.example.back.user.security.CustomUserDetails;
import com.example.back.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /*
     * 회원 가입
     * */
    @PostMapping
    public ResponseEntity<Long> register(@Valid @RequestBody UserDto.UserCreateRequest request) {

        log.info("REST 요청 - 등록: {}", request);

        // 등록
        Long id = userService.register(request);
        log.info("등록된 id 값: {}", id);

        // 응답과 생성된 id 전달
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(id);
    }

    // =========================================================================
    // [ME 영역] 로그인한 사용자 본인의 자원 (GET, PATCH, DELETE /me)
    // =========================================================================

    /*
     * [본인] 내 정보 상세 조회
     * - 토큰에 담긴 본인 정보만 조회 가능
     * */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto.UserReadResponse> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("내 정보 조회 요청 - ID: {}", userDetails.getId());

        UserDto.UserReadResponse response = userService.getOneById(userDetails.getId());
        return ResponseEntity.ok(response);
    }


    /*
     * [본인] 내 정보 수정
     * - 토큰에 담긴 본인 정보만 수정 가능
     * */
    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> modifyMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserDto.UserUpdateRequest updateDto) {

        userService.modify(userDetails.getId(), updateDto);
        return ResponseEntity.ok(Map.of("result", "success"));
    }

    /*
     * [본인] 회원 탈퇴 (논리 삭제)
     * */
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> remove(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("REST 요청 - 회원 탈퇴(Soft Delete) ID: {}", userDetails.getId());

        // 삭제 상태값만 변경
        userService.removeSoft(userDetails.getId());

        return ResponseEntity.ok(Map.of("result", "success", "message", "회원 탈퇴가 완료되었습니다."));
    }

    // =========================================================================
    // [ADMIN 영역] 관리자에 의한 사용자 제어 (GET, PATCH, DELETE /admin/...)
    // =========================================================================

    /*
     * [관리자] 특정 사용자 상세 조회
     * */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto.UserReadResponse> getOne(
            @PathVariable("id") Long id) {

        log.info("REST 요청 - 상세 조회 ID: {}", id);

        UserDto.UserReadResponse response = userService.getOneById(id);
        log.info("조회 정보: {}", response);

        return ResponseEntity.ok(response);
    }


    /*
     * [관리자] 특정 사용자 수정
     * */
    @PatchMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> modify(
            @PathVariable("id") Long id,
            @RequestBody UserDto.UserUpdateRequest updateDto) {
        log.info("REST 요청 - 수정 ID: {}, 데이터: {}", id, updateDto);
        userService.modify(id, updateDto);

        return ResponseEntity.ok(Map.of("result", "success"));
    }


    /*
     * [관리자] 회원 탈퇴 (물리 삭제)
     * */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> removeHard(
            @PathVariable("id") Long id) {
        log.info("REST 요청 - 삭제 ID: {}", id);
        userService.remove(id);

        return ResponseEntity.ok(Map.of("result", "success"));
    }

    /*
    * [관리자] 페이징 없는 목록 조회
    * */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto.UserListResponse>> getAllList() {
        log.info("REST 요청 - 전체 목록 조회");
        List<UserDto.UserListResponse> list = userService.getAllList();
        return ResponseEntity.ok(list);
    }


    /*
     * [관리자] 페이징 목록 조회
     * */
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponseDto<UserDto.UserListResponse>> getList(
            PageRequestDto pageRequestDto) {

        log.info("REST 요청 - 페이징 조회: page={}, size={}", 
                pageRequestDto.getPage(), pageRequestDto.getSize());

        PageResponseDto<UserDto.UserListResponse> response = userService.getListPage(pageRequestDto);
        log.info("페이징 목록 : {}", response);

        return ResponseEntity.ok(response);
    }
}
