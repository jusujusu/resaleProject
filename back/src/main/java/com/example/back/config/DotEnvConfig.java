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

        //  만약 현재 경로에서 DB_URL을 못 찾았다면 (상위 폴더에서 실행 중일 가능성)
        //    ./back 폴더 안을 다시 찾아봅니다.
        if (dotenv.get("DB_URL") == null) {
            dotenv = Dotenv.configure()
                    .directory("./back")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
        }

        /*
        * DotEnv 로드된 값을 기반으로 순회하여 properteis 값 세팅
        */
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue()));

    }

}
