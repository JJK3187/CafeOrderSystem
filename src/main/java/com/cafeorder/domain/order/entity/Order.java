package com.cafeorder.domain.order.entity;

import com.cafeorder.config.entity.BaseEntity;
import com.cafeorder.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(name = "order_num", unique = true, nullable = false)
    private UUID orderNum;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order createOrder(
            BigDecimal totalPrice,
            User user
    ) {
        Order order = new Order();
        order.totalPrice = totalPrice;
        order.orderStatus = OrderStatus.PENDING;
        order.user = user;
        return order;
    }

    public static Order createEmptyOrder(User user) {
        Order order = new Order();
        order.totalPrice = BigDecimal.ZERO;
        order.orderStatus = OrderStatus.PENDING;
        order.user = user;
        return order;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        BigDecimal newTotal = this.totalPrice.add(orderItem.calculateTotalPrice());
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("총 금액이 음수가 될 수 없습니다.");
        }
        this.totalPrice = newTotal;
    }

    /** 결제 완료 시 호출 */
    public void complete() {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 주문만 완료 처리할 수 있습니다.");
        }
        this.orderStatus = OrderStatus.COMPLETED;
    }

    /** 결제 실패 시 호출 */
    public void fail() {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 주문만 실패 처리할 수 있습니다.");
        }
        this.orderStatus = OrderStatus.FAILED;
    }
}
