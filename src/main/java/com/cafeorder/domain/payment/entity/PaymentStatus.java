package com.cafeorder.domain.payment.entity;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("PENDING", "결제 대기 중"),
    COMPLETED("COMPLETED", "결제 완료"),
    FAILED("FAILED", "결제 실패"),
    CANCELLED("CANCELLED", "결제 취소");

    private final String code;
    private final String description;
    PaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

