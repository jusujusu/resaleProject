package com.example.back.user.entity;

import com.example.back.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

/**
 * 회원 관련 테이블 엔티티
 *
 * @fileName : UserEntity
 * @since : 26. 3. 23.
 */
@Entity
@Table(name = "users")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")   // 생성시 삭제 기본값 설정
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 식별자 (PK)

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 로그인 아이디로 사용되는 이메일 (Unique)

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, length = 50)
    private String nickname; // 서비스 내에서 표시될 이름

    @Column(nullable = false, length = 50)
    private String name; // 가입시 필요한 이름

    @Column(nullable = false)
    private String address; // 기본 주소

    @Column(name = "detail_address")
    private String detailAddress; // 상세 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;      // 사용자 권한


    // -------------- 빌더 패턴 --------------
    @Builder
    public UserEntity(String email, String password, String phoneNumber, String nickname, String name, String address, String detailAddress, UserRole role) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.name = name;
        this.address = address;
        this.detailAddress = detailAddress;
        this.role = (role != null) ? role : UserRole.USER;    // 기본값 USER
    }


    // -------------- 비즈니스 로직 --------------
    /*
    * 회원 정보 수정 (patch 방식을 사용하기 때문에 아래처럼 작성)
    * */
    public void updateProfile(String nickname, String address, String detailAddress, String phoneNumber) {
        if (nickname != null && !nickname.trim().isEmpty()) {this.nickname = nickname;}
        if (address != null && !address.trim().isEmpty()) {this.address = address;}
        if (detailAddress != null) {this.detailAddress = detailAddress;}
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {this.phoneNumber = phoneNumber;}
    }

    /*
    * 비밀번호 변경
    * */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /*
    * 권한 수정
    * */
    public void changeRole(UserRole role) {
        this.role = role;
    }

}
