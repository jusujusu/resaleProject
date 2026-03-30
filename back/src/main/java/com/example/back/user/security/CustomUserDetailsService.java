package com.example.back.user.security;

import com.example.back.user.entity.UserEntity;
import com.example.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * FileName    : CustomUserDetailsService
 * Since       : 26. 3. 30.
 * Dsecription  : DB에서 유저 정보를 조회하여 시큐리티에 전달
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 이메일로 유저 조회
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // DB에서 이메일로 찾기
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다.: " + email));

        // 찾은 유저 정보를 UserDetails에 담아서 반환
        return new CustomUserDetails(user);
    }
}
