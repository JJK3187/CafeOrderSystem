package com.cafeorder.domain.menu.service;

import com.cafeorder.domain.menu.dto.MenuResponse;
import com.cafeorder.domain.menu.dto.PopularMenuResponse;
import com.cafeorder.domain.menu.repository.MenuRepository;
import com.cafeorder.domain.order.entity.OrderStatus;
import com.cafeorder.domain.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private static final int  POPULAR_MENU_LIMIT  = 3;
    private static final int  POPULAR_MENU_DAYS   = 7;

    private final MenuRepository     menuRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getAllMenus() {
        return menuRepository.findAll().stream()
                .map(MenuResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PopularMenuResponse> getPopularMenus() {
        LocalDateTime since = LocalDateTime.now().minusDays(POPULAR_MENU_DAYS);

        return orderItemRepository.findPopularMenusSince(
                OrderStatus.COMPLETED,
                since,
                PageRequest.of(0, POPULAR_MENU_LIMIT)
        );
    }
}
