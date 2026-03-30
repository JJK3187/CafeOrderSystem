package com.cafeorder.domain.point.entity;

import com.cafeorder.config.entity.BaseEntity;
import com.cafeorder.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 낙관적 락: 동시 충전/결제 요청 시 포인트 잔액 정합성 보장 */
    @Version
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id", unique = true)
    private User user;

    @Column(nullable = false, name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    public static Point createPoint(User user) {
        Point point = new Point();
        point.user = user;
        point.balance = BigDecimal.ZERO;
        return point;
    }

    public void charge(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.balance = this.balance.add(amount);
    }

    public void use(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("포인트 잔액이 부족합니다.");
        }
        this.balance = this.balance.subtract(amount);
    }
}
