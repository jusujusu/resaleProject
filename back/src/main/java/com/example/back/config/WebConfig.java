package com.example.back.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * FileName    : WebConfig
 * Since       : 26. 3. 24.
 * Dsecription  : 프론트와 통신을 위한 CORS 설정, 파일 이미지 설정
 * TODO 스프링 시큐리티 설정시 CORS 설정은 SecurityConfig로 이동
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${FILE_PATH}")
    private String fileDir;


    /*
     * security 설정 전 cors 설정
     * */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")          // api로 시작하는 모든 경로에 적용
                .allowedOrigins("http://localhost:5173")  //  react 기본 포트 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*")        // 모든 헤더 허용
                .allowCredentials(true)     // 쿠키나 인증 헤더를 허용할 경우 핈후
                .exposedHeaders("Authorization")
                .maxAge(3600);              // 브라우저가 preflight 요청을 캐싱하는 시간 (초)

        // 이미지 경로에도 CORS 허용
        registry.addMapping("/images/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*");

    }


    /*
    * 외부에서 접근하는 가상 경로와 실제 로컬 저장소의 물리 경로 설정
    * */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저가 /images/** 로 요청하면 C:/uploads/ 폴더에서 파일을 찾음
        registry.addResourceHandler("/images/**")
                .addResourceLocations(fileDir);
    }
}
