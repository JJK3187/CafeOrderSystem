package com.cafeorder.domain.point.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class AddPointsRequest {
    private BigDecimal amount;
}
