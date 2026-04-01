package com.example.back.user.controller;

import com.example.back.user.dto.AuthDto;
import com.example.back.user.dto.UserDto;
import com.example.back.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * FileName    : AuthController
 * Since       : 26. 3. 31.
 * Dsecription  : 인증 관련 Controller
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;


    /*
     * 로그인
     * */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> login(
            @RequestBody UserDto.UserLoginRequest loginRequest,
            HttpServletResponse response) {

        log.info("!!로그인 요청 : {}", loginRequest.getEmail());

        // 토큰 생성
        AuthDto.TokenResponse tokenResponse = userService.login(loginRequest);


        // RefreshToken을 HttpOnly 쿠키로 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)       // JS 접근 불가 (XSS 방어)
                .secure(true)         // HTTPS에서만 전송
                .path("/")            // 모든 경로에서 유효
                .maxAge(7 * 24 * 60 * 60) // 7일 (ms가 아니라 초 단위입니다)
                .sameSite("Strict")   // CSRF 방어
                .build();

        // 응답 헤더에 쿠키 추가
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // AccessToken만 포함된 객체를 반환
        return ResponseEntity.ok(tokenResponse);
    }


    /*
     * 로그아웃
     * */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false)String refreshToken,
            HttpServletResponse response) {

        log.info(">>> [로그아웃 요청]");

        // Redis에서 토큰 삭제
        if (refreshToken != null) {
            userService.logout(refreshToken);
        }

        // 쿠키를 즉시 만료시킴
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)  // 0초로 설정하여 삭제
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.noContent().build();
    }


    /*
     * 토큰 재발급
     * */
    @PostMapping("/reissue")
    public ResponseEntity<AuthDto.TokenResponse> reissue(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response) {

        log.info(">>> [재발급 요청]");

        // 서비스에서 검증 및 재발급 로직 수행
        AuthDto.TokenResponse newTokenSet = userService.reissue(refreshToken);

        // 새 RefreshToken 쿠키 설정 (Rotation 적용 시)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newTokenSet.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        // 응답 헤더에 쿠키 추가
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(newTokenSet);
    }
}
