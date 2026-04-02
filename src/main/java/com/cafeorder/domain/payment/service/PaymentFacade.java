package com.cafeorder.domain.payment.service;

import com.cafeorder.domain.payment.dto.PaymentRequest;
import com.cafeorder.domain.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final DataPlatformService dataPlatformService;

    public PaymentResponse processPayment(Long orderId, PaymentRequest request) {

        // 1. 결제 처리 — @Transactional, 이 줄이 return되면 DB 커밋 완료
        PaymentResponse response = paymentService.processPayment(orderId, request);

        // 2. 커밋 이후 — 외부 데이터 플랫폼 비동기 전송 (@Async → 별도 스레드)
        dataPlatformService.sendAsync(
                response.getUserId(),
                response.getMenuIds(),
                response.getPaymentAmount(),
                response.getOrderId()
        );

        return response;
    }
}