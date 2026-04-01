package com.cafeorder.domain.order.service;

import com.cafeorder.config.exception.ServiceException;
import com.cafeorder.config.exception.ErrorCode;
import com.cafeorder.domain.menu.entity.Menu;
import com.cafeorder.domain.menu.entity.MenuStatus;
import com.cafeorder.domain.menu.repository.MenuRepository;
import com.cafeorder.domain.order.dto.CreateOrderRequest;
import com.cafeorder.domain.order.dto.CreateOrderResponse;
import com.cafeorder.domain.order.entity.Order;
import com.cafeorder.domain.order.entity.OrderItem;
import com.cafeorder.domain.order.repository.OrderRepository;
import com.cafeorder.domain.users.entity.User;
import com.cafeorder.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateOrderResponse saveOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        Set<Long> menuIds = request.getItems().stream()
                .map(CreateOrderRequest.OrderLineRequest::getMenuId)
                .collect(Collectors.toSet());

        List<Menu> menus = menuRepository.findAllById(menuIds);
        if (menus.size() != menuIds.size()) {
            throw new ServiceException(ErrorCode.MENU_NOT_FOUND);
        }

        Map<Long, Menu> menuById = menus.stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));

        Order order = Order.createEmptyOrder(user);

        for (CreateOrderRequest.OrderLineRequest item : request.getItems()) {
            Menu menu = menuById.get(item.getMenuId());
            
            // 메뉴 판매 상태 검증
            if (menu.getMenuStatus() != MenuStatus.ON_SALE) {
                throw new ServiceException(ErrorCode.MENU_NOT_ON_SALE);
            }
            
            // 재고 검증
            if (menu.getStockQuantity() < item.getQuantity()) {
                throw new ServiceException(
                    ErrorCode.INSUFFICIENT_STOCK,
                    String.format("메뉴 '%s'의 재고가 부족합니다. (요청: %d, 보유: %d)",
                    menu.getName(), item.getQuantity(), menu.getStockQuantity())
                );
            }

            OrderItem orderItem = OrderItem.createOrderItem(order, menu, item.getQuantity());
            order.addOrderItem(orderItem); 
        }

        Order savedOrder = orderRepository.save(order);
        return CreateOrderResponse.from(savedOrder);
    }
}
