package com.example.back.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 *  DotEnv 환경설정
 *  @fileName : DotEnvConfig
 *  @since : 26. 03. 17.
 */
@Configuration
public class DotEnvConfig {

    static {

        /*
         * DotEnv 로드 및 데이터 조회
         */
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        /*
        * DotEnv 로드된 값을 기반으로 순회하여 properteis 값 세팅
        */
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue()));

    }

}
