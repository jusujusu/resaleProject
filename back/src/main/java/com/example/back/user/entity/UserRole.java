package com.example.back.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자의 권한 상태
 *
 * @fileName : UserRole
 * @since : 26. 3. 23.
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER("USER", "일반 사용자"),
    ADMIN("ADMIN", "관리자");

    private final String key;
    private final String title;

}
