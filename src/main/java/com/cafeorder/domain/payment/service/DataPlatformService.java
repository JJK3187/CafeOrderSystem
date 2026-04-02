package com.cafeorder.domain.payment.service;

import com.cafeorder.infra.platform.DataPlatformClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPlatformService {

    private final DataPlatformClient dataPlatformClient;

    @Async("dataPlatformExecutor")
    public void sendAsync(Long userId, List<Long> menuIds, BigDecimal paymentAmount, Long orderId) {
        log.info("[DataPlatform] 비동기 전송 시작 → orderId={}, userId={}", orderId, userId);
        try {
            dataPlatformClient.sendOrderData(userId, menuIds, paymentAmount);
        } catch (Exception e) {
            // 외부 플랫폼 오류는 결제 결과에 영향을 주지 않음
            log.error("[DataPlatform] 전송 실패 → orderId={}, error={}", orderId, e.getMessage(), e);
        }
    }
}