package com.example.back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger(OpenAPI) 환경설정
 *
 * @fileName : SwaggerConfig
 * @since : 26. 3. 17.
 */

@Configuration
public class SwaggerConfig {

    /**
     * Swagger UI의 핵심 설정 빈(Bean) 등록
     */
    @Bean
    public OpenAPI openAPI() {
        /*
         * 1. 전역 설정을 위한 OpenAPI 객체 생성
         * 2. Components: 보안(JWT) 설정을 위한 보관소 연결
         * 3. Info: 문서 제목 및 버전 정보 연결
         */
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }


    private Info apiInfo() {
        /**
         * API 문서 상단에 표기될 제목, 설명, 버전 설정
         */
        return new Info()
                .title("API Test") // API의 제목
                .description("Let's practice Swagger UI") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }

}
