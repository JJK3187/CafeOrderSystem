package com.cafeorder.domain.payment.controller;

import com.cafeorder.config.exception.ApiResponse;
import com.cafeorder.domain.payment.dto.PaymentRequest;
import com.cafeorder.domain.payment.dto.PaymentResponse;
import com.cafeorder.domain.payment.service.PaymentFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request
    ) {
        PaymentResponse response = paymentFacade.processPayment(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}