package com.example.back.user.service;

import com.example.back.dto.PageRequestDto;
import com.example.back.dto.PageResponseDto;
import com.example.back.user.dto.AuthDto;
import com.example.back.user.dto.UserDto;
import com.example.back.user.entity.UserEntity;
import com.example.back.user.repository.UserRepository;
import com.example.back.user.security.CustomUserDetails;
import com.example.back.user.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    /*
    * 회원 가입
    * */
    @Transactional
    public Long register(UserDto.UserCreateRequest request) {

        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        log.info("회원 가입 요청: {}", request);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity userEntity = request.toEntity(encodedPassword);

        log.info("회원가입 결과: {}", userEntity);
        return userRepository.save(userEntity).getId();
    }


    /*
     * 로그인 및 토큰 생성
     * */
    @Transactional
    public AuthDto.TokenResponse login(UserDto.UserLoginRequest request) {

        // 사용자 확인
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        // 토큰 세트 생성 (Access + Refresh)
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 토큰 반환
        return jwtProvider.createTokenSet(authentication);
    }


    /*
     * 토큰 재발급 (Refresh Token 활용)
     * */
    @Transactional
    public AuthDto.TokenResponse reissue(String refreshToken) {

        // Refresh Token 검증 (만료 여부 및 서명 확인)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }

        // 토큰에서 사용자 정보(Authentication) 추출
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);

        // 유저가 실제로 존재하는지만 체크
        // Redis 사용 시 Refresh Token과 일치하는지 확인하는 로직으로 변경
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 새로운 토큰 세트 생성 및 반환
        return jwtProvider.createTokenSet(authentication);
    }


    /*
     * 이메일로 상세 조회
     * */
    public UserDto.UserReadResponse getOne(String email) {

        log.info("입력한 Email : {}", email);
        Optional<UserEntity> result = userRepository.findByEmail(email);

        // 존재하지 않을 시 예외 처리
        UserEntity entity = result.orElseThrow(() -> new RuntimeException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));

        log.info(">>> 조회 결과: {}", result);
        return UserDto.UserReadResponse.from(entity);
    }

    /*
     * ID를 통해 단건 상세 정보를 조회
     * */
    public UserDto.UserReadResponse getOneById(Long id) {
        log.info("Fetching User with ID: {}", id);
        Optional<UserEntity> result = userRepository.findById(id);
        UserEntity entity = result.orElseThrow(() -> new RuntimeException("해당 데이터를 찾을 수 없습니다. ID: " + id));
        return UserDto.UserReadResponse.from(entity);
    }

    /*
     * 일반 페이징 목록을 조회
     * */
    public PageResponseDto<UserDto.UserListResponse> getListPage(PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable("id");
        Page<UserEntity> result = userRepository.findAll(pageable);
        return convertToPageResponse(result);
    }

    /*
     * 페이징 없는 전체 목록을 조회
     * */
    public List<UserDto.UserListResponse> getAllList() {
        List<UserEntity> entities = userRepository.findAll();
        return entities.stream()
                .map(UserDto.UserListResponse::from)
                .collect(Collectors.toList());
    }

    /*
     * 데이터를 수정
     * */
    @Transactional
    public void modify(Long id, UserDto.UserUpdateRequest updateDto) {
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
        Optional<UserEntity> result = userRepository.findByIdIncludeDeleted(id);
        UserEntity entity = result.orElseThrow(() -> new RuntimeException("삭제할 사용자를 찾을 수 없습니다. ID: " + id));
        userRepository.delete(entity);
        log.info("ID {}번 데이터 삭제 완료", id);
    }

    /*
     * [공통 로직] Page 결과를 공통 응답 DTO로 변환
     * */
    private PageResponseDto<UserDto.UserListResponse> convertToPageResponse(Page<UserEntity> result) {
        List<UserDto.UserListResponse> dtoList = result.getContent().stream()
                .map(UserDto.UserListResponse::from)
                .collect(Collectors.toList());
        return new PageResponseDto<>(result, dtoList);
    }


}
