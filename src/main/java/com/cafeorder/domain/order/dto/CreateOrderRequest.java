package com.cafeorder.domain.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateOrderRequest {

	@NotNull(message = "사용자 식별값은 필수입니다.")
	private Long userId;

	@NotEmpty(message = "주문 항목은 1개 이상이어야 합니다.")
	@Valid
	private List<OrderLineRequest> items;

	@Getter
	@NoArgsConstructor
	public static class OrderLineRequest {

		@NotNull(message = "메뉴 ID는 필수입니다.")
		private Long menuId;

		@Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
		private int quantity;
	}
}
