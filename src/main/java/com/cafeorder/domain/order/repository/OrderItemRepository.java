package com.cafeorder.domain.order.repository;

import com.cafeorder.domain.menu.dto.PopularMenuResponse;
import com.cafeorder.domain.order.entity.OrderItem;
import com.cafeorder.domain.order.entity.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 특정 기간 내 COMPLETED 주문을 기준으로 메뉴별 주문 횟수를 집계합니다.
     *
     * <p>집계 기준:</p>
     * <ul>
     *   <li>결제 완료(COMPLETED) 상태의 주문만 포함</li>
     *   <li>주문 생성 시각(createdAt)이 since 이후인 항목만 포함</li>
     *   <li>메뉴별 주문 횟수(order_items 행 수) 기준으로 내림차순 정렬</li>
     * </ul>
     *
     * @param status   집계 대상 주문 상태 (COMPLETED)
     * @param since    집계 시작 시각 (현재 시각 - 7일)
     * @param pageable 상위 N개 제한에 사용 (PageRequest.of(0, 3))
     */
    @Query("""
            SELECT new com.cafeorder.domain.menu.dto.PopularMenuResponse(
                m.id,
                m.name,
                COUNT(oi.id)
            )
            FROM OrderItem oi
            JOIN oi.order o
            JOIN oi.menu m
            WHERE o.orderStatus = :status
              AND o.createdAt  >= :since
            GROUP BY m.id, m.name
            ORDER BY COUNT(oi.id) DESC
            """)
    List<PopularMenuResponse> findPopularMenusSince(
            @Param("status") OrderStatus status,
            @Param("since") LocalDateTime since,
            Pageable pageable
    );
}

