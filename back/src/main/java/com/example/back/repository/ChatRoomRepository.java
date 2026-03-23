package com.example.back.repository;

import com.example.back.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 *
 * @fileName : ChatRoomRepository
 * @since : 26. 3. 23.
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    /*
     * 내가 판매자이거나 구매자인 채팅방 목록 최신순 조회
     * */
    @Query("SELECT r FROM ChatRoomEntity r WHERE r.seller.id = :userId OR r.buyer.id = :userId ORDER BY r.createdAt DESC")
    List<ChatRoomEntity> findMyChatRooms(@Param("userId") Long userId);

    /*
    *
    * */

}
