package com.example.back.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 상품 상태 관리
 *
 * @fileName : ProductStatus
 * @since : 26. 3. 23.
 */

@Getter
@RequiredArgsConstructor
public enum ProductStatus {

    SALE("판매중"),
    RESERVED("예약중"),
    SOLD("판매완료"),
    HIDDEN("숨김"); // 사용자 요청에 의한 미노출

    private final String description;
}
