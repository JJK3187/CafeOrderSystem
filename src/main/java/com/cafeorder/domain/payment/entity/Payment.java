package com.cafeorder.domain.payment.entity;

import com.cafeorder.config.entity.BaseEntity;
import com.cafeorder.domain.order.entity.Order;
import com.cafeorder.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(name = "payment_num", unique = true, nullable = false)
    private UUID paymentNum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "payment_amount")
    private BigDecimal paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "failure_reason")
    private String failureReason;

    public static Payment createPendingPayment(
            Order order,
            User user,
            BigDecimal paymentAmount
    ) {
        Payment payment = new Payment();
        payment.order = order;
        payment.user = user;
        payment.paymentAmount = paymentAmount;
        payment.paymentStatus = PaymentStatus.PENDING;
        return payment;
    }

    public void complete() {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 결제만 완료할 수 있습니다.");
        }
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void fail(String failureReason) {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalStateException("대기 상태의 결제만 실패 처리할 수 있습니다.");
        }
        this.paymentStatus = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }

    public void cancel(String cancellationReason) {
        if (this.paymentStatus != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("완료된 결제만 취소할 수 있습니다.");
        }
        this.paymentStatus = PaymentStatus.CANCELLED;
        this.failureReason = cancellationReason;
    }
}
