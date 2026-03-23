package com.example.back.entity;

import com.example.back.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 채팅방 엔티티
 *
 * @fileName : ChatRoomEntity
 * @since : 26. 3. 23.
 */

@Entity
@Table
@Getter
@ToString(exclude = {"product", "seller", "buyer"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 식별자 (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;  // 관련 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private UserEntity seller; // 판매자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private UserEntity buyer; // 구매 희망자


    // -------------- 빌더 패턴 --------------
    @Builder
    public ChatRoomEntity(ProductEntity product, UserEntity seller, UserEntity buyer) {
        this.product = product;
        this.seller = seller;
        this.buyer = buyer;
    }
}
