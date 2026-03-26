package com.example.back.user.dto;

import com.example.back.user.entity.UserEntity;
import com.example.back.user.entity.UserRole;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    public static class UserCreateRequest {
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
        
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 4, message = "비밀번호는 최소 4자 이상입니다.")
        private String password;
        
        @NotBlank(message = "전화번호는 필수 입력 값입니다.")
        @Pattern(regexp = "^[0-9]+$", message = "전화번호는 숫자만 입력 가능합니다.")
        private String phoneNumber;
        
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        private String nickname;

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;
        
        @NotBlank(message = "주소는 필수 입력 값입니다.")
        private String address;
        
        private String detailAddress;

        public UserEntity toEntity() {
            return UserEntity.builder()
                    .email(email)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .nickname(nickname)
                    .name(name)
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
    public static class UserUpdateRequest {
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickname;
        
        @NotBlank(message = "주소를 입력해주세요.")
        private String address;
        
        private String detailAddress;
        
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(regexp = "^[0-9]+$", message = "전화번호는 숫자만 입력 가능합니다.")
        private String phoneNumber;
    }

    // 비밀번호 변경 요청 (Password Change)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ToString
    public static class UserPasswordChangeRequest {
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
    public static class UserLoginRequest {
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
    public static class UserReadResponse {
        private Long id;
        private String email;
        private String phoneNumber;
        private String nickname;
        private String name;
        private String address;
        private String detailAddress;
        private UserRole role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static UserReadResponse from(UserEntity entity) {
            return UserReadResponse.builder()
                    .id(entity.getId())
                    .email(entity.getEmail())
                    .phoneNumber(entity.getPhoneNumber())
                    .nickname(entity.getNickname())
                    .name(entity.getName())
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
    public static class UserListResponse {
        private Long id;
        private String email;
        private String nickname;
        private LocalDateTime createdAt;

        public static UserListResponse from(UserEntity entity) {
            return UserListResponse.builder()
                    .id(entity.getId())
                    .email(entity.getEmail())
                    .nickname(entity.getNickname())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }

}
