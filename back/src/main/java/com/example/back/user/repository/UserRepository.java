package com.example.back.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back.user.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 사용자 Repository
 *
 * @fileName : UserRepository
 * @since : 26. 3. 23.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /*
    * 이메일로 사용자 조회
    * */
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);


}
