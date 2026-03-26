package com.example.back.user.service;

import com.example.back.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

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

        UserDto.UserCreateRequest request = UserDto.UserCreateRequest.builder()
                .email("test" + System.currentTimeMillis() + "@mail.com")
                .password("1234")
                .phoneNumber("010-1234-5678")
                .nickname("새내기개발자")
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

        UserDto.UserReadResponse response = userService.getOne(email);

        log.info(">>> 조회 결과: {}", response);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getId()).isNotNull();

    }

    @Test
    @DisplayName("ID로 1건 조회 테스트")
    void readOneByIdTest() {

        // 먼저 회원가입
        UserDto.UserCreateRequest request = UserDto.UserCreateRequest.builder()
                .email("test" + System.currentTimeMillis() + "@mail.com")
                .password("1234")
                .phoneNumber("010-1234-5678")
                .nickname("ID조회테스트")
                .address("경기도 부천시")
                .detailAddress("OO아파트 101호")
                .build();

        Long savedId = userService.register(request);

        // ID로 조회
        UserDto.UserReadResponse response = userService.getOneById(savedId);

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
        UserDto.UserUpdateRequest updateRequest = UserDto.UserUpdateRequest.builder()
                .nickname("수정후닉네임")
                .address("서울시 강남구")
                .detailAddress("XX빌딩 202호")
                .phoneNumber("010-9876-5432")
                .build();

        // 수정 실행
        userService.modify(id, updateRequest);
        log.info(">>> 수정 요청 정보: {}", updateRequest);

        // 수정된 데이터 조회 및 검증
        UserDto.UserReadResponse updatedResponse = userService.getOneById(id);
        log.info(">>> 수정 후 회원 정보: {}", updatedResponse);

        assertThat(updatedResponse.getNickname()).isEqualTo(updateRequest.getNickname());
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
    
    @Test
    @DisplayName("유저 50명 생성")
    void createDummyUsers() {
        log.info("========== 유저 50명 생성 시작 ==========");

        // 지정한 횟수만큼 순회하며 호출결과를 리스트로 수집
        List<Long> savedIds = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> {
                    UserDto.UserCreateRequest request = UserDto.UserCreateRequest.builder()
                            .email("t" + i + "@example.com")
                            .password("password123!")
                            .phoneNumber("010-0000-" + String.format("%04d", i))
                            .nickname("테스트닉네임" + i)
                            .name("사용자" + i)
                            .address("서울특별시 강남구")
                            .detailAddress("테헤란로 " + i + "길")
                            .build();

                    // 실제 서비스의 단일 가입 메서드 호출 및 생성된 ID 반환
                    return userService.register(request);
                })
                .toList();

        log.info("저장된 유저 수: {}", savedIds.size());
        assertThat(savedIds).hasSize(50);
        assertThat(savedIds).allMatch(id -> id != null);

        log.info("========== 더미 유저 50명 생성 종료 ==========");
    }


}
