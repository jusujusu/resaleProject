package com.example.back.repository;

import com.example.back.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 *
 * @fileName : ChatMessageRepository
 * @since : 26. 3. 23.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    /*
    *  특정 방의 메시지 목록을 시간 순으로 가져옴
    * */
    @Query("SELECT m FROM ChatMessageEntity m WHERE m.room.id = :roomId ORDER BY m.sentAt ASC")
    List<ChatMessageEntity> findMessagesByRoomId(@Param("roomId") Long roomId);

}
