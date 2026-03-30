package com.cafeorder.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("PENDING", "대기"),
    COMPLETED("COMPLETED", "완료"),
    FAILED("FAILED", "실패");

    private final String code;
    private final String description;
    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
