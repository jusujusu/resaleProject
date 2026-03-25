package com.example.back.user.service;

import com.example.back.dto.PageRequestDto;
import com.example.back.dto.PageResponseDto;
import com.example.back.user.dto.UserDto;
import com.example.back.user.entity.UserEntity;
import com.example.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 사용자 비즈니스 로직
 *
 * @fileName : UserService
 * @since : 26. 3. 24.
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;


    /*
    * 회원 가입
    * */
    @Transactional
    public Long register(UserDto.CreateRequest request) {

        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        log.info("회원 가입 요청: {}", request);
        UserEntity userEntity = request.toEntity();

        log.info("회원가입 결과: {}", userEntity);
        return userRepository.save(userEntity).getId();
    }

    /*
     * 이메일로 상세 조회
     * */
    public UserDto.ReadResponse getOne(String email) {

        log.info("입력한 Email : {}", email);
        Optional<UserEntity> result = userRepository.findByEmail(email);

        // 존재하지 않을 시 예외 처리
        UserEntity entity = result.orElseThrow(() -> new RuntimeException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));

        log.info(">>> 조회 결과: {}", result);
        return UserDto.ReadResponse.from(entity);
    }

    /*
     * ID를 통해 단건 상세 정보를 조회
     * */
    public UserDto.ReadResponse getOneById(Long id) {
        log.info("Fetching User with ID: {}", id);
        Optional<UserEntity> result = userRepository.findById(id);
        UserEntity entity = result.orElseThrow(() -> new RuntimeException("해당 데이터를 찾을 수 없습니다. ID: " + id));
        return UserDto.ReadResponse.from(entity);
    }

    /*
     * 일반 페이징 목록을 조회
     * */
    public PageResponseDto<UserDto.ListResponse> getListPage(PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable("id");
        Page<UserEntity> result = userRepository.findAll(pageable);
        return convertToPageResponse(result);
    }

    /*
     * 페이징 없는 전체 목록을 조회
     * */
    public List<UserDto.ListResponse> getAllList() {
        List<UserEntity> entities = userRepository.findAll();
        return entities.stream()
                .map(UserDto.ListResponse::from)
                .collect(Collectors.toList());
    }

    /*
     * 데이터를 수정
     * */
    @Transactional
    public void modify(Long id, UserDto.UpdateRequest updateDto) {
        log.info("수정 요청 ID: {}", id);
        Optional<UserEntity> result = userRepository.findById(id);
        UserEntity entity = result.orElseThrow(() -> new RuntimeException("수정할 데이터를 찾을 수 없습니다. ID: " + id));
        
        // 비즈니스 로직 수행
        entity.updateProfile(
                updateDto.getNickname(),
                updateDto.getAddress(),
                updateDto.getDetailAddress(),
                updateDto.getPhoneNumber()
        );
    }

    /*
     *  [논리 삭제] 삭제 여부 플래그만 변경 (일반 회원용)
     * */
    @Transactional
    public void removeSoft(Long id) {

        log.info("논리 삭제 요청 ID: {}", id);
        Optional<UserEntity> result = userRepository.findById(id);
        UserEntity entity = result.orElseThrow(() -> new RuntimeException("삭제할 사용자를 찾을 수 없습니다. ID: " + id));

        // BaseTimeEntity의 메소드 호출
        entity.softDelete();

        log.info("ID {}번 유저 비활성화(is_deleted=true) 완료", id);

    }

    /*
     * [물리 삭제] 데이터 실제 삭제 (관리자용)
     * */
    @Transactional
    public void remove(Long id) {
        log.info("물리 삭제 요청 ID: {}", id);
        Optional<UserEntity> result = userRepository.findById(id);
        UserEntity entity = result.orElseThrow(() -> new RuntimeException("삭제할 사용자를 찾을 수 없습니다. ID: " + id));
        userRepository.delete(entity);
        log.info("ID {}번 데이터 삭제 완료", id);
    }

    /*
     * [공통 로직] Page 결과를 공통 응답 DTO로 변환
     * */
    private PageResponseDto<UserDto.ListResponse> convertToPageResponse(Page<UserEntity> result) {
        List<UserDto.ListResponse> dtoList = result.getContent().stream()
                .map(UserDto.ListResponse::from)
                .collect(Collectors.toList());
        return new PageResponseDto<>(result, dtoList);
    }


}
