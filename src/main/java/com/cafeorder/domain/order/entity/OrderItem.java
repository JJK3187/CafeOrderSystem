package com.cafeorder.domain.order.entity;

import com.cafeorder.config.entity.BaseEntity;
import com.cafeorder.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "menu_name", nullable = false)
    private String menuName; // 주문 시점 메뉴명 스냅샷

    @Column(name = "menu_price", nullable = false)
    private BigDecimal menuPrice; // 주문 당시 메뉴 가격 스냅샷

    @Column(nullable = false)
    private int quantity;

    public static OrderItem createOrderItem(Order order, Menu menu, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.order = order;
        orderItem.menu = menu;

        // 메뉴에서 현재 상태의 값을 꺼냄
        orderItem.menuName = menu.getName();
        orderItem.menuPrice = menu.getPrice();
        orderItem.quantity = quantity;

        return orderItem;
    }

    // 스냅샷 가격 * 수량
    public BigDecimal calculateTotalPrice() {
        return this.menuPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
