package com.cafeorder.domain.order.dto;

import com.cafeorder.domain.order.entity.Order;
import com.cafeorder.domain.order.entity.OrderItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CreateOrderResponse {

	private final Long orderId;
	private final UUID orderNum;
	private final String orderStatus;
	private final BigDecimal totalPrice;
	private final List<OrderLineResponse> items;

	public static CreateOrderResponse from(Order order) {
		List<OrderLineResponse> lines = order.getOrderItems().stream()
				.map(OrderLineResponse::from)
				.toList();
		return new CreateOrderResponse(
				order.getId(),
				order.getOrderNum(),
				order.getOrderStatus().name(),
				order.getTotalPrice(),
				lines
		);
	}

	@Getter
	@RequiredArgsConstructor
	public static class OrderLineResponse {
		private final Long menuId;
		private final String menuName;
		private final BigDecimal menuPrice;
		private final int quantity;

		public static OrderLineResponse from(OrderItem item) {
			return new OrderLineResponse(
					item.getMenu().getId(),
					item.getMenuName(),
					item.getMenuPrice(),
					item.getQuantity()
			);
		}
	}
}
