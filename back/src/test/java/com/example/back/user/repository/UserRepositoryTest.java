package com.example.back.user.repository;

import com.example.back.user.entity.UserEntity;
import com.example.back.user.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원 정보 저장 테스트")
    void creteTest() {
        log.info("========== 회원 생성 테스트 시작 ==========");

        UserEntity user = UserEntity.builder()
                .email("create@test.com")
                .password("1234")
                .nickName("새회원")
                .phoneNumber("010-1111-2222")
                .address("부천시")
                .role(UserRole.USER)
                .build();

        UserEntity savedUser = userRepository.save(user);

        log.info("저장된 회원 ID: {}", savedUser.getId());
        log.info("저장된 회원 이메일: {}", savedUser.getEmail());


        assertThat(savedUser.getId()).isNotNull();
        log.info("========== 회원 생성 테스트 종료 ==========");
    }

    @Test
    @DisplayName("회원 10명 추가 테스트")
    void createMultipleUsersTest() {
        log.info("========== 회원 10명 생성 테스트 시작 ==========");

        List<UserEntity> users = IntStream.range(0, 10)
                .mapToObj(i -> UserEntity.builder()
                        .email("user" + i + "@test.com")
                        .password("1234")
                        .nickName("사용자" + i)
                        .phoneNumber("010-1234-567" + i)
                        .address("서울시")
                        .detailAddress("강남구 " + i + "번지")
                        .build())
                .toList();

        List<UserEntity> savedUsers = userRepository.saveAll(users);

        log.info("저장된 회원 수: {}", savedUsers.size());
        savedUsers.forEach(user -> 
            log.info("ID: {}, Email: {}, NickName: {}, Role: {}", 
                user.getId(), user.getEmail(), user.getNickName(), user.getRole()));

        assertThat(savedUsers).hasSize(10);
        assertThat(savedUsers).allMatch(user -> user.getId() != null);
        assertThat(savedUsers).allMatch(user -> user.getEmail().contains("@test.com"));
        
        log.info("========== 회원 10명 생성 테스트 종료 ==========");
    }

    @Test
    @DisplayName("회원 목록 조회 테스트")
    void readList() {
        log.info("========== 회원 목록 조회 테스트 시작 ==========");

        // 전체 회원 목록 조회
        List<UserEntity> allUsers = userRepository.findAll();
        
        log.info("전체 회원 수: {}", allUsers.size());
        
        // 목록이 비어있지 않은지 확인
        assertThat(allUsers).isNotEmpty();
        
        // 각 회원의 기본 정보 출력
        allUsers.forEach(user -> 
            log.info("회원 정보 - ID: {}, Email: {}, NickName: {}, Role: {}", 
                user.getId(), user.getEmail(), user.getNickName(), user.getRole()));

        // 첫 번째 회원과 마지막 회원 정보 확인
        if (!allUsers.isEmpty()) {
            UserEntity firstUser = allUsers.get(0);
            UserEntity lastUser = allUsers.get(allUsers.size() - 1);
            
            log.info("첫 번째 회원: ID={}, Email={}", firstUser.getId(), firstUser.getEmail());
            log.info("마지막 회원: ID={}, Email={}", lastUser.getId(), lastUser.getEmail());
            
            assertThat(firstUser.getId()).isNotNull();
            assertThat(lastUser.getId()).isNotNull();
        }

        log.info("========== 회원 목록 조회 테스트 종료 ==========");
    }

    @Test
    @DisplayName("회원 조회 테스트")
    void readTest() {
        log.info("========== 회원 조회 테스트 시작 ==========");

        // 기존 데이터 중 첫 번째 회원 조회
        UserEntity existingUser = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("테스트용 회원 데이터가 없습니다."));

        log.info("사용할 기존 회원: ID={}, Email={}", existingUser.getId(), existingUser.getEmail());

        // ID로 조회
        UserEntity foundUser = userRepository.findById(existingUser.getId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(existingUser.getEmail());

        // 이메일로 조회
        UserEntity foundByEmail = userRepository.findByEmail(existingUser.getEmail()).orElse(null);
        assertThat(foundByEmail).isNotNull();
        assertThat(foundByEmail.getId()).isEqualTo(existingUser.getId());

        log.info("조회된 회원: ID={}, Email={}, NickName={}", 
            foundUser.getId(), foundUser.getEmail(), foundUser.getNickName());
        log.info("========== 회원 조회 테스트 종료 ==========");
    }

    @Test
    @DisplayName("회원 정보 수정 테스트")
    void updateTest() {
        log.info("========== 회원 수정 테스트 시작 ==========");

        // 기존 데이터 중 첫 번째 회원 조회
        UserEntity existingUser = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("테스트용 회원 데이터가 없습니다."));

        log.info("수정 전 회원: ID={}, NickName={}, Address={}", 
            existingUser.getId(), existingUser.getNickName(), existingUser.getAddress());


        // 정보 수정
        existingUser.updateProfile("수정후닉네임", "수정후주소", "수정후상세주소", "010-9999-8888");
        existingUser.changeRole(UserRole.ADMIN);
        
        UserEntity updatedUser = userRepository.save(existingUser);

        // 수정된 정보 확인
        UserEntity foundUser = userRepository.findById(updatedUser.getId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getNickName()).isEqualTo("수정후닉네임");
        assertThat(foundUser.getAddress()).isEqualTo("수정후주소");
        assertThat(foundUser.getDetailAddress()).isEqualTo("수정후상세주소");
        assertThat(foundUser.getPhoneNumber()).isEqualTo("010-9999-8888");
        assertThat(foundUser.getRole()).isEqualTo(UserRole.ADMIN);

        log.info("수정된 회원: NickName={}, Address={}, Role={}", 
            foundUser.getNickName(), foundUser.getAddress(), foundUser.getRole());


        log.info("========== 회원 수정 테스트 종료 ==========");
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void deleteTest() {
        log.info("========== 회원 삭제 테스트 시작 ==========");

        Long userId = 37L;

        // 삭제 전 확인
        assertThat(userRepository.findById(userId)).isPresent();

        UserEntity foundUser = userRepository.findById(userId).orElse(null);

        log.info("삭제할 회원: NickName={}, Address={}, Role={}",
                foundUser.getNickName(), foundUser.getAddress(), foundUser.getRole());

        // 삭제
        userRepository.delete(foundUser);

        // 삭제 후 확인
        assertThat(userRepository.findById(userId)).isEmpty();

        log.info("삭제된 회원 ID: {}", userId);
        log.info("========== 회원 삭제 테스트 종료 ==========");
    }








}
