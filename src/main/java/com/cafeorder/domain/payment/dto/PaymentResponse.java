package com.cafeorder.domain.payment.dto;

import com.cafeorder.domain.payment.entity.Payment;
import com.cafeorder.domain.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal paymentAmount;
    private PaymentStatus paymentStatus;
    private String failureReason;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .paymentNum(payment.getPaymentNum())
                .orderId(payment.getOrder().getId())
                .userId(payment.getUser().getId())
                .paymentAmount(payment.getPaymentAmount())
                .paymentStatus(payment.getPaymentStatus())
                .failureReason(payment.getFailureReason())
                .build();
    }
}
