package com.cafeorder.domain.point.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class AddPointsRequest {
    @NotNull(message = "충전 금액은 필수입니다.")
    @DecimalMin(value = "0.01", message = "충전 금액은 0보다 커야 합니다.")
    private BigDecimal amount;
}
