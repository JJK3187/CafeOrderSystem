package com.cafeorder.domain.order.service;

import com.cafeorder.config.exception.ServiceException;
import com.cafeorder.domain.menu.entity.Menu;
import com.cafeorder.domain.menu.entity.MenuStatus;
import com.cafeorder.domain.menu.repository.MenuRepository;
import com.cafeorder.domain.order.dto.CreateOrderRequest;
import com.cafeorder.domain.order.dto.CreateOrderResponse;
import com.cafeorder.domain.order.entity.Order;
import com.cafeorder.domain.order.entity.OrderStatus;
import com.cafeorder.domain.order.repository.OrderRepository;
import com.cafeorder.domain.users.entity.User;
import com.cafeorder.domain.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_returnsTotalPriceAndSnapshotItems() {
        User user = User.createUser("tester");
        ReflectionTestUtils.setField(user, "id", 1L);

        Menu latte = Menu.createMenu("Latte", BigDecimal.valueOf(5000), 10, MenuStatus.ON_SALE);
        Menu mocha = Menu.createMenu("Mocha", BigDecimal.valueOf(4500), 10, MenuStatus.ON_SALE);
        ReflectionTestUtils.setField(latte, "id", 11L);
        ReflectionTestUtils.setField(mocha, "id", 22L);

        CreateOrderRequest request = new CreateOrderRequest();
        ReflectionTestUtils.setField(request, "userId", 1L);
        CreateOrderRequest.OrderLineRequest i1 = new CreateOrderRequest.OrderLineRequest();
        ReflectionTestUtils.setField(i1, "menuId", 11L);
        ReflectionTestUtils.setField(i1, "quantity", 2);
        CreateOrderRequest.OrderLineRequest i2 = new CreateOrderRequest.OrderLineRequest();
        ReflectionTestUtils.setField(i2, "menuId", 22L);
        ReflectionTestUtils.setField(i2, "quantity", 1);
        ReflectionTestUtils.setField(request, "items", List.of(i1, i2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(menuRepository.findAllByIdInWithLock(any())).thenReturn(List.of(latte, mocha));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 100L);
            ReflectionTestUtils.setField(order, "orderNum", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
            return order;
        });

        CreateOrderResponse response = orderService.saveOrder(request);

        assertEquals(100L, response.getOrderId());
        assertEquals(OrderStatus.PENDING.name(), response.getOrderStatus());
        assertEquals(BigDecimal.valueOf(14500), response.getTotalPrice());
        assertEquals(2, response.getItems().size());
    }

    @Test
    void createOrder_throwsWhenMenuIsNotOnSale() {
        User user = User.createUser("tester");
        ReflectionTestUtils.setField(user, "id", 1L);

        Menu soldOut = Menu.createMenu("Latte", BigDecimal.valueOf(5000), 0, MenuStatus.SOLD_OUT);
        ReflectionTestUtils.setField(soldOut, "id", 11L);

        CreateOrderRequest request = new CreateOrderRequest();
        ReflectionTestUtils.setField(request, "userId", 1L);
        CreateOrderRequest.OrderLineRequest item = new CreateOrderRequest.OrderLineRequest();
        ReflectionTestUtils.setField(item, "menuId", 11L);
        ReflectionTestUtils.setField(item, "quantity", 1);
        ReflectionTestUtils.setField(request, "items", List.of(item));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(menuRepository.findAllByIdInWithLock(any())).thenReturn(List.of(soldOut));

        assertThrows(ServiceException.class, () -> orderService.saveOrder(request));
    }
}


