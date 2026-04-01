package com.example.back.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * FileName    : JwtAuthenticationFilter
 * Since       : 26. 3. 30.
 * Dsecription  : 요청 헤더의 JWT를 검사하여 인증 정보를 등록하는 필터
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 토큰이 유효한지 확인하고, 유효하면 인증 정보를 SecurityContext에 저장
        if (token != null) {
            log.debug("추출된 토큰 발견: {}", token.substring(0, 10) + "...");

            if (jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("SecurityContext에 인증 정보 저장 완료: {}", authentication.getName());
            } else {
                log.warn("토큰이 유효하지 않습니다.");
            }
        }

        // 다음 필터 진행
        filterChain.doFilter(request, response);
    }


    /*
     * 내부 로직 : Authorization 헤더에서 Bearer 토큰 문자열만 추출
     * */
    private String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

}
