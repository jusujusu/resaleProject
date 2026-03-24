package com.example.back.user.service;

import com.example.back.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FileName    : UserServiceTest
 * Since       : 26. 3. 24.
 * Dsecription  :
 */

@Slf4j
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;


    @Test
    @DisplayName("회원가입 테스트")
    void createTest() {
        log.info("========== 회원 생성 테스트 시작 ==========");

        UserDto.CreateRequest request = UserDto.CreateRequest.builder()
                .email("test" + System.currentTimeMillis() + "@mail.com")
                .password("1234")
                .phoneNumber("010-1234-5678")
                .nickName("새내기개발자")
                .address("경기도 부천시")
                .detailAddress("OO아파트 101호")
                .build();

        log.info(">>> 회원 가입 시도: {}", request.getEmail());
        Long savedId = userService.register(request);

        log.info(">>> 가입 완료! 생성된 ID: {}", savedId);

        assertThat(savedId).isNotNull();

    }


    @Test
    @DisplayName("1건 조회 테스트")
    void readOneTest() {

        String email = "create@test.com";

        UserDto.ReadResponse response = userService.getOne(email);

        log.info(">>> 조회 결과: {}", response);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getId()).isNotNull();

    }

    @Test
    @DisplayName("ID로 1건 조회 테스트")
    void readOneByIdTest() {

        // 먼저 회원가입
        UserDto.CreateRequest request = UserDto.CreateRequest.builder()
                .email("test" + System.currentTimeMillis() + "@mail.com")
                .password("1234")
                .phoneNumber("010-1234-5678")
                .nickName("ID조회테스트")
                .address("경기도 부천시")
                .detailAddress("OO아파트 101호")
                .build();

        Long savedId = userService.register(request);

        // ID로 조회
        UserDto.ReadResponse response = userService.getOneById(savedId);

        log.info(">>> ID 조회 결과: {}", response);
        assertThat(response.getId()).isEqualTo(savedId);
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("회원 수정 테스트")
    void modifyTest() {

        Long id = 1L;
        
        log.info(">>> ID {}번 회원 정보 수정", id);

        // 수정 요청
        UserDto.UpdateRequest updateRequest = UserDto.UpdateRequest.builder()
                .nickName("수정후닉네임")
                .address("서울시 강남구")
                .detailAddress("XX빌딩 202호")
                .phoneNumber("010-9876-5432")
                .build();

        // 수정 실행
        userService.modify(id, updateRequest);
        log.info(">>> 수정 요청 정보: {}", updateRequest);

        // 수정된 데이터 조회 및 검증
        UserDto.ReadResponse updatedResponse = userService.getOneById(id);
        log.info(">>> 수정 후 회원 정보: {}", updatedResponse);

        assertThat(updatedResponse.getNickName()).isEqualTo(updateRequest.getNickName());
        assertThat(updatedResponse.getAddress()).isEqualTo(updateRequest.getAddress());
        assertThat(updatedResponse.getDetailAddress()).isEqualTo(updateRequest.getDetailAddress());
        assertThat(updatedResponse.getPhoneNumber()).isEqualTo(updateRequest.getPhoneNumber());
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void removeTest() {

        Long id = 43L;
        
        log.info(">>> ID {}번 회원 삭제", id);

        // 삭제 실행
        userService.remove(id);
        log.info(">>> 삭제 완료");

        // 삭제된 데이터 조회 시 예외 발생 확인
        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> userService.getOneById(id)
        );
    }





}
