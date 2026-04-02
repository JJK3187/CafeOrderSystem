package com.cafeorder.domain.payment.service;

import com.cafeorder.domain.payment.dto.PaymentRequest;
import com.cafeorder.domain.payment.dto.PaymentResponse;
import com.cafeorder.domain.payment.entity.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private DataPlatformService dataPlatformService;

    @InjectMocks
    private PaymentFacade paymentFacade;

    @Test
    @DisplayName("결제 성공 후 DataPlatformService.sendAsync가 호출된다")
    void processPayment_callsSendAsyncAfterSuccess() {
        // given
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(8000));

        PaymentResponse stubResponse = PaymentResponse.builder()
                .paymentId(200L)
                .orderId(100L)
                .userId(1L)
                .menuIds(List.of(10L, 20L))
                .paymentAmount(BigDecimal.valueOf(8000))
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        when(paymentService.processPayment(100L, request)).thenReturn(stubResponse);

        // when
        PaymentResponse response = paymentFacade.processPayment(100L, request);

        // then
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);

        // PaymentService 커밋 후 DataPlatformService 비동기 호출 검증
        verify(dataPlatformService, times(1)).sendAsync(
                1L,
                List.of(10L, 20L),
                BigDecimal.valueOf(8000),
                100L
        );
    }

    @Test
    @DisplayName("PaymentService에서 예외 발생 시 DataPlatformService는 호출되지 않는다")
    void processPayment_doesNotSendWhenPaymentFails() {
        // given
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(8000));
        when(paymentService.processPayment(100L, request))
                .thenThrow(new RuntimeException("결제 실패"));

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> paymentFacade.processPayment(100L, request)
        );

        // 결제 실패 → 플랫폼 전송 없음
        verify(dataPlatformService, never()).sendAsync(any(), any(), any(), any());
    }
}

