package com.example.back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger(OpenAPI) 환경설정
 *
 * @fileName : SwaggerConfig (JWT 사용 버전)
 * @since : 26. 3. 17.
 */

@Configuration
public class SwaggerConfig {

    /**
     * JWT 인증 설정이 포함된 Swagger UI 빈 등록
     */
    @Bean
    public OpenAPI openAPI() {
        /*
         * 1. 보안 요구사항(SecurityRequirement) 정의
         * Swagger UI에서 특정 API를 호출할 때 어떤 보안 스키마를 사용할지 지정합니다.
         */
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        /*
         * 2. 보안 스키마(SecurityScheme) 구성
         * 실제 토큰 인증 방식(Bearer, JWT 등)과 헤더에 들어갈 상세 정보를 설정합니다.
         */
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

        /*
         * 3. 최종 OpenAPI 객체 생성 및 반환
         * 기본 정보(Info)와 보안 설정(Security)을 결합합니다.
         */
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    /**
     * API 문서 상단에 표기될 기본 정보 설정
     */
    private Info apiInfo() {
        return new Info()
                .title("API Test")                     // API의 제목
                .description("Let's practice Swagger UI") // API에 대한 설명
                .version("1.0.0");                    // API의 버전
    }
}