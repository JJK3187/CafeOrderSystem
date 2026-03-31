package com.cafeorder.domain.point.entity;

import com.cafeorder.config.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "point_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "point_id")
    private Point point;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;


    public static PointHistory create(
            Point point,
            TransactionType type,
            BigDecimal amount
    ) {
        PointHistory history = new PointHistory();
        history.point = point;
        history.type = type;
        history.amount = amount;
        return history;
    }

    // 포인트 변동 타입을 관리하는 내부 Enum
    public enum TransactionType {
        CHARGE,  // 충전
        USE      // 사용 (결제)
    }
}
