package com.example.back.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * FileName    : WebConfig
 * Since       : 26. 3. 24.
 * Dsecription  : 파일 이미지 설정 (CORS 설정은 SecurityConfig로 이동됨)
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${FILE_PATH}")
    private String fileDir;

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
