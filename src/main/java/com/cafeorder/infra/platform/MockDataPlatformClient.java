package com.cafeorder.infra.platform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class MockDataPlatformClient implements DataPlatformClient {

    @Override
    public void sendOrderData(Long userId, List<Long> menuIds, BigDecimal paymentAmount) {
        log.info("[DataPlatform] 주문 데이터 전송 → userId={}, menuIds={}, paymentAmount={}",
                userId, menuIds, paymentAmount);

        // 실제 외부 API 호출 시뮬레이션 (네트워크 지연 가정)
        // RestTemplate / WebClient 등으로 대체 가능
        simulateApiCall(userId, menuIds, paymentAmount);

        log.info("[DataPlatform] 주문 데이터 전송 완료 → userId={}", userId);
    }

    private void simulateApiCall(Long userId, List<Long> menuIds, BigDecimal paymentAmount) {
        // Mock: 실제 HTTP 요청 대신 성공으로 처리
        log.debug("[DataPlatform] Mock API 호출 성공 → payload={{ userId={}, menuIds={}, paymentAmount={} }}",
                userId, menuIds, paymentAmount);
    }
}

