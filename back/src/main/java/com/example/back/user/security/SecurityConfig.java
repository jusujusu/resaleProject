package com.example.back.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * FileName    : SecurityConfig
 * Since       : 26. 3. 30.
 * Dsecription  : 시큐리티 설정 및 필터 체인 구성
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)    // @PreAuthorize 등의 어노테이션 사용 가능하게 설정
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;


    /*
    * [비밀번호 암호화]
    * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * [인증 매니저] 로그인 서비스에서 인증을 수동으로 진행할 때 필요
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    /*
    * [보안 필터 체인]
    * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // JWT 사용을 위한 불필요 기능 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable) // 기존 코드의 withDefaults()에서 변경됨

                // stateless 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 권한 및 경로 설정
                .authorizeHttpRequests(auth -> auth
                        // 비인증 허용 - POST API
                        .requestMatchers(HttpMethod.POST,        // post 방식의 아래 url에 적용
                                "/api/v1/user",         // 회원 가입
                                "/api/v1/auth/login",           // 로그인
                                "/api/v1/auth/logout",          // 로그아웃
                                "/api/v1/auth/reissue"          // 토큰 재발급
                        ).permitAll()

                        // 비인증 허용 - Swagger 및 설정 관련
                        .requestMatchers(
                                "/v3/api-docs/**",    // Swagger가 만드는 API 명세 데이터 경로
                                "/api-docs/**",
                                "/swagger-ui/**",     // Swagger UI 리소스 경로
                                "/swagger-ui.html",    // Swagger UI 메인 페이지
                                "/swagger-resources/**",   // Swagger 리소스
                                "/webjars/**",             // Swagger 웹 자원
                                "/favicon.ico",  // 파비콘 에러 방지
                                "/error"         // 에러 페이지 접근 허용
                        ).permitAll() // [비인증 허용] 로그인, 회원가입 등 인증이 필요 없는 API 경로는 모두에게 개방함

                        // [권한 제한] 관리자 전용 기능은 'ADMIN' 역할을 가진 유저만 접근 가능함
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // [인증 필수] 위 설정 외의 모든 요청은 유효한 JWT 토큰이 있어야만 접근 가능 (로그인이 필요함)
                        .anyRequest().authenticated()
                )

                // CustomUserDetailsService 시큐리티에 등록
                .userDetailsService(customUserDetailsService)

                // JWT 필터를 시큐리티 인증 필터 앞에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);




        return http.build();
    }
}
