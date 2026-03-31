package com.example.back.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * FileName    : AuthDto
 * Since       : 26. 3. 30.
 * Dsecription  : JWT 토큰 응답 및 재발급 요청 등 인증 관련 DTO 통합 관리
 */
public class AuthDto {

    // 반환 토큰
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class TokenResponse {
        private String grantType;    // 인증 타입 (Bearer)
        private String accessToken;  // 서비스 접근용 토큰
        private String refreshToken; // 토큰 재발급용 토큰

        /*
         * 토큰 정보를 바탕으로 표준 응답 객체 ㅅ갱성
         * */
        public static TokenResponse of(String accessToken, String refreshToken) {
            return TokenResponse.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

    }

}
