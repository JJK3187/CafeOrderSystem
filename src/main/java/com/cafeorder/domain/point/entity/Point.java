package com.cafeorder.domain.point.entity;

import com.cafeorder.config.entity.BaseEntity;
import com.cafeorder.config.exception.ServiceException;
import com.cafeorder.config.exception.ErrorCode;
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
            throw new ServiceException(ErrorCode.INVALID_POINT_AMOUNT, "충전 금액은 0보다 커야 합니다.");
        }
        this.balance = this.balance.add(amount);
    }

    public void use(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(ErrorCode.INVALID_POINT_AMOUNT, "사용 금액은 0보다 커야 합니다.");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new ServiceException(ErrorCode.INSUFFICIENT_POINT);
        }
        this.balance = this.balance.subtract(amount);
    }
}
