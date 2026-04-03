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

