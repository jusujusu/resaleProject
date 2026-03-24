package com.example.back.user.dto;

import com.example.back.user.entity.UserEntity;
import com.example.back.user.entity.UserRole;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

/**
 * User 관련 데이터 전송 객체 통합 관리
 * 용도별로 내부 클래스를 분리하여 관리 포인트 최적화
 *
 * @fileName : UserDto
 * @since : 26. 3. 24.
 */
public class UserDto {

    // 등록 요청 (Create) - 회원가입용, 모든 필드 검증 적용
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class CreateRequest {
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
        
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        private String password;
        
        @NotBlank(message = "전화번호는 필수 입력 값입니다.")
        private String phoneNumber;
        
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        private String nickName;
        
        @NotBlank(message = "주소는 필수 입력 값입니다.")
        private String address;
        
        private String detailAddress;

        public UserEntity toEntity() {
            return UserEntity.builder()
                    .email(email)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .nickName(nickName)
                    .address(address)
                    .detailAddress(detailAddress)
                    .role(UserRole.USER)
                    .build();
        }
    }

    // 수정 요청 (Update) - 프로필 수정 가능한 필드만 제한적으로 구성
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class UpdateRequest {
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickName;
        
        @NotBlank(message = "주소를 입력해주세요.")
        private String address;
        
        private String detailAddress;
        
        @NotBlank(message = "전화번호를 입력해주세요.")
        private String phoneNumber;
    }

    // 비밀번호 변경 요청 (Password Change)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class PasswordChangeRequest {
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        private String currentPassword;
        
        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        private String newPassword;
    }

    // 로그인 요청 (Login)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class LoginRequest {
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;
        
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        private String password;
    }

    // 상세 조회 응답 - 모든 상세 정보 포함 (비밀번호 제외)
    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class ReadResponse {
        private Long id;
        private String email;
        private String phoneNumber;
        private String nickName;
        private String address;
        private String detailAddress;
        private UserRole role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ReadResponse from(UserEntity entity) {
            return ReadResponse.builder()
                    .id(entity.getId())
                    .email(entity.getEmail())
                    .phoneNumber(entity.getPhoneNumber())
                    .nickName(entity.getNickName())
                    .address(entity.getAddress())
                    .detailAddress(entity.getDetailAddress())
                    .role(entity.getRole())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    // 목록 조회
    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class ListResponse {
        private Long id;
        private String email;
        private String nickName;
        private LocalDateTime createdAt;

        public static ListResponse from(UserEntity entity) {
            return ListResponse.builder()
                    .id(entity.getId())
                    .email(entity.getEmail())
                    .nickName(entity.getNickName())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }

}
