package com.cafeorder.infra.platform;

import java.math.BigDecimal;
import java.util.List;

public interface DataPlatformClient {

    void sendOrderData(Long userId, List<Long> menuIds, BigDecimal paymentAmount);
}

