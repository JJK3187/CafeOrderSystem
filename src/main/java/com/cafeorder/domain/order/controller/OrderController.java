package com.cafeorder.domain.order.controller;

import com.cafeorder.config.exception.ApiResponse;
import com.cafeorder.domain.order.dto.CreateOrderRequest;
import com.cafeorder.domain.order.dto.CreateOrderResponse;
import com.cafeorder.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        CreateOrderResponse response = orderService.saveOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
