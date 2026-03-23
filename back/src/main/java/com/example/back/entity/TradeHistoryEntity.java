package com.example.back.entity;

import com.example.back.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 거래 기록 엔티티
 *
 * @fileName : TradeHistoryEntity
 * @since : 26. 3. 23.
 */

@Entity
@Table(name = "trade_histories")
@Getter
@ToString(exclude = {"product", "seller", "buyer"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 식별자 (PK)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product; // 거래된 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private UserEntity seller; // 판매자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private UserEntity buyer; // 구매자

    private LocalDateTime tradedAt; // 거래 완료 시간


    // -------------- 빌더 패턴 --------------
    @Builder
    public TradeHistoryEntity(ProductEntity product, UserEntity seller, UserEntity buyer) {
        this.product = product;
        this.seller = seller;
        this.buyer = buyer;
        this.tradedAt = LocalDateTime.now();
    }

}
