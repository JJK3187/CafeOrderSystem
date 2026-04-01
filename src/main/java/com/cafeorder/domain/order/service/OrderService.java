package com.cafeorder.domain.order.service;

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

import java.math.BigDecimal;
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
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Set<Long> menuIds = request.getItems().stream()
                .map(CreateOrderRequest.OrderLineRequest::getMenuId)
                .collect(Collectors.toSet());

        List<Menu> menus = menuRepository.findAllById(menuIds);
        if (menus.size() != menuIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 메뉴가 포함되어 있습니다.");
        }

        Map<Long, Menu> menuById = menus.stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderLineRequest item : request.getItems()) {
            Menu menu = menuById.get(item.getMenuId());
            if (menu.getMenuStatus() != MenuStatus.ON_SALE) {
                throw new IllegalArgumentException("판매 중이 아닌 메뉴는 주문할 수 없습니다. menuId=" + item.getMenuId());
            }
            totalPrice = totalPrice.add(menu.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Order order = Order.createOrder(totalPrice, user);

        for (CreateOrderRequest.OrderLineRequest item : request.getItems()) {
            Menu menu = menuById.get(item.getMenuId());
            OrderItem orderItem = OrderItem.createOrderItem(order, menu, item.getQuantity());
            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        return CreateOrderResponse.from(savedOrder);
    }
}
