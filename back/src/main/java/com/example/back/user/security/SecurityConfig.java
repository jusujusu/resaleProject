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
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
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
                .authorizeHttpRequests(this::configureAuthorization)

                // CustomUserDetailsService 시큐리티에 등록
                .userDetailsService(customUserDetailsService)

                // JWT 필터를 시큐리티 인증 필터 앞에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    /*
    * [상세 권한 및 경로 설정]
    * - 구체적인 경로가 일반적인 경로보다 먼저 와야 함
    *
    * [경로 구체성 예시]
    * /api/v1/user/me      ← 구체적 (먼저)
    * /api/v1/user/{id}    ← 중간
    * /api/v1/user/**      ← 일반적 (나중)
    */
    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                // [누구나 접근 가능]
                .requestMatchers(
                        "/v3/api-docs/**", "/api-docs/**", "/swagger-ui/**",
                        "/swagger-ui.html", "/swagger-resources/**", "/webjars/**",
                        "/favicon.ico", "/error"                                        // swagger 관련
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/user").permitAll()  // 회원가입
                .requestMatchers("/api/v1/auth/**").permitAll()                // 로그인, 로그아웃, 토큰 재발급

                // [일반 사용자 API - 로그인 인증 필요]
                .requestMatchers("/api/v1/user/me").authenticated()

                // [관리자 전용 API - ROLE_ADMIN 권한 필요]
                // ※ 세부 권한은 컨트롤러의 @PreAuthorize와 2중으로 보호됨
                .requestMatchers("/api/v1/user/admin/**").hasRole("ADMIN")

                // [나머지 전체 잠금]
                .anyRequest().authenticated();
    }

}
