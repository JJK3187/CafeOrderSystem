package com.cafeorder.domain.payment.dto;

import com.cafeorder.domain.payment.entity.Payment;
import com.cafeorder.domain.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long paymentId;
    private UUID paymentNum;
    private Long orderId;
    private Long userId;
    private List<Long> menuIds;       // 결제된 메뉴 ID 목록 (플랫폼 전송용)
    private BigDecimal paymentAmount;
    private PaymentStatus paymentStatus;
    private String failureReason;

    public static PaymentResponse from(Payment payment) {
        List<Long> menuIds = payment.getOrder().getOrderItems().stream()
                .map(item -> item.getMenu().getId())
                .toList();

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .paymentNum(payment.getPaymentNum())
                .orderId(payment.getOrder().getId())
                .userId(payment.getUser().getId())
                .menuIds(menuIds)
                .paymentAmount(payment.getPaymentAmount())
                .paymentStatus(payment.getPaymentStatus())
                .failureReason(payment.getFailureReason())
                .build();
    }
}