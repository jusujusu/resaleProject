package com.example.back.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class test {

    @Value("${DB_USERNAME}")
    private String dbUsername;

    @GetMapping("/hello")
    public String hello() {

        log.info("로그로그로그!!!!!!");
        log.info("현재 연결된 DB 사용자명: {}", dbUsername);

        return "Hello, Spring Boot!";

    }

}
