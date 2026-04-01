package com.cafeorder.domain.payment.controller;

import com.cafeorder.domain.payment.dto.PaymentRequest;
import com.cafeorder.domain.payment.dto.PaymentResponse;
import com.cafeorder.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentRequest request
    ) {
        try {
            PaymentResponse response = paymentService.processPayment(orderId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}

