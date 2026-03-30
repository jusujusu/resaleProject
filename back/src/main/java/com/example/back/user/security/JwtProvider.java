package com.example.back.user.security;

import com.example.back.user.dto.AuthDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * FileName    : JwtProvider
 * Since       : 26. 3. 30.
 * Dsecription  : JWT 토큰 생성, 복호화, 유효성 검증을 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private SecretKey key;

    private final CustomUserDetailsService userDetailsService;


    /*
     * 빈 생성 및 의존성 주입 후 실행되어 보안 키를 생성
     * */
    @PostConstruct
    public void init() {
        // 주입받은 secret 문자열을 byte 배열로 변환하여 HMAC 알고리즘용 키를 생성
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * 토큰을 이용해 Authentication 객체를 생성
     * 필터에서 토큰이 유효할 때 이 메서드를 호출하여 시큐리티 컨텍스트에 저장
     * */
    public Authentication getAuthentication(String token) {

        // 토큰에서 이메일(subject) 추출
        String email = getUsername(token);

        // DB에서 유저 정보 로드 (CustomUserDetails 반환)
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // 시큐리티용 인증 토큰 생성 (비밀번호는 이미 토큰으로 인증되었으므로 null)
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

    }


    /*
     * 인증된 객체(Authentication)를 받아 accessToken, refreshToken 한 번에 생성해 DTO로 반환
     * */
    public AuthDto.TokenResponse createTokenSet(Authentication authentication) {

        // 인증 객체에서 UserDetails를 꺼냄
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // accessToken, refreshToken 한 번에 생성
        String accessToken = generateAccessToken(userDetails);
        String refreshToken = generateRefreshToken(userDetails);

        // DTO로 반환
        return AuthDto.TokenResponse.of(accessToken, refreshToken);

    }


    /*
    * 사용자의 정보를 바탕으로 accessToken 생성
    * */
    public String generateAccessToken(UserDetails userDetails) {

        // 토큰 발행 시간을 현재 시점으로 설정
        Date now = new Date();

        // 현재 시간에 설정된 만료 시간을 더하여 최종 만료 시점을 계산
        Date expiry = new Date(now.getTime() + accessExpirationMs);

        // 사용자가 가진 여러 권한들을 콤마로 구분된 하나의 문자열로 결합
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        return Jwts.builder()
                .subject(userDetails.getUsername())     // 토큰 제목
                .claim("auth", authorities)          // 사용자 권한 클레임
                .issuedAt(now)                          // 발행 시간
                .expiration(expiry)                     // 액섹스 토큰 만료 시간
                .signWith(key)                          // 보안 키 서명
                .compact();
    }


    /*
     * 재발급을 위해 사용자 식별 정보만 담은  refreshToken 생성
     * */
    public String generateRefreshToken(UserDetails userDetails) {

        // 토큰 발행 시간을 현재 시점으로 설정
        Date now = new Date();

        // 현재 시간에 설정된 만료 시간을 더하여 최종 만료 시점을 계산
        Date expiry = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())     // 토큰 제목
                .expiration(expiry)                     // 액섹스 토큰 만료 시간
                .signWith(key)                          // 보안 키 서명
                .compact();
    }


    /*
     * 토큰 유효성 검증
     * */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 비어있습니다.");
        }
        return false;
    }



    /*
     * 토큰 내부에서 사용자 식별값 추출
     * */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /*
     * 내부 로직 : 토큰 복호화 및 페이로드 추출
     * */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
