package com.example.back.entity;

import com.example.back.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅방 메시지 엔티티
 *
 * @fileName : ChatMessageEntity
 * @since : 26. 3. 23.
 */
@Entity
@Table
@Getter
@ToString(exclude = {"room", "sender"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 식별자 (PK)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message; // 메시지 내용

    private LocalDateTime sentAt; // 보낸 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoomEntity room; // 해당 채팅방

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "")
    private UserEntity sender;  // 보낸 사람


    // -------------- 빌더 패턴 --------------
    @Builder
    public ChatMessageEntity(String message, ChatRoomEntity room, UserEntity sender) {
        this.message = message;
        this.room = room;
        this.sender = sender;
        this.sentAt = LocalDateTime.now();
    }
}
