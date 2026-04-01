package com.example.back.user.security;

import com.example.back.user.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * FileName    : CustomUserDetails
 * Since       : 26. 3. 30.
 * Dsecription  : 스프링 시큐리티 인증 객체
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    // 유저의 권한을 시큐리티 규격으로 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 엔티티의 Role 앞에 "ROLE_" 추가해 시큐리티가 인식하게 함
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    // 암호화된 비밀번호 반환
    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    // 로그인 시 필요한 이름 (현재 프로젝트는 email로 로그인)
    @Override
    public String getUsername() {
        return user.getEmail();
    }


    /* --- 계정 상태 관리 (필요 시 로직 추가, 기본은 true) --- */
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    /*
     * 컨트롤러에서 userDetails.getId()로
     * PK 값을 바로 꺼내 쓰기 위해 추가한 편의 메서드
     * */
    public Long getId() {
        return user.getId();
    }

    /*
    * 서비스 계층에서 권한을 문자열로 편하게 비교하기 위해 추가
    * */
    public String getRole() {
        return "ROLE_" + user.getRole().name();
    }
}
