package com.cafeorder.domain.payment.service;

import com.cafeorder.config.exception.ErrorCode;
import com.cafeorder.config.exception.ServiceException;
import com.cafeorder.domain.menu.entity.Menu;
import com.cafeorder.domain.menu.entity.MenuStatus;
import com.cafeorder.domain.order.entity.Order;
import com.cafeorder.domain.order.entity.OrderItem;
import com.cafeorder.domain.order.repository.OrderRepository;
import com.cafeorder.domain.payment.dto.PaymentRequest;
import com.cafeorder.domain.payment.dto.PaymentResponse;
import com.cafeorder.domain.payment.entity.Payment;
import com.cafeorder.domain.payment.entity.PaymentStatus;
import com.cafeorder.domain.payment.repository.PaymentRepository;
import com.cafeorder.domain.point.entity.Point;
import com.cafeorder.domain.point.repository.PointRepository;
import com.cafeorder.domain.users.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private Menu menu;
    private Order order;
    private Point point;

    @BeforeEach
    void setUp() {
        user = User.createUser("tester");
        ReflectionTestUtils.setField(user, "id", 1L);

        menu = Menu.createMenu("Americano", BigDecimal.valueOf(4000), 10, MenuStatus.ON_SALE);
        ReflectionTestUtils.setField(menu, "id", 10L);

        order = Order.createEmptyOrder(user);
        ReflectionTestUtils.setField(order, "id", 100L);
        OrderItem item = OrderItem.createOrderItem(order, menu, 2);
        order.addOrderItem(item);
        // totalPrice = 8000

        point = Point.createPoint(user);
        point.charge(BigDecimal.valueOf(10000));
        ReflectionTestUtils.setField(point, "id", 1L);
    }

    @Test
    @DisplayName("결제 성공 시 COMPLETED 상태와 menuIds를 포함한 응답을 반환한다")
    void processPayment_successReturnsCompletedResponseWithMenuIds() {
        // given
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(8000));
        when(orderRepository.findByIdWithLock(100L)).thenReturn(Optional.of(order));
        when(pointRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(point));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            ReflectionTestUtils.setField(p, "id", 200L);
            return p;
        });

        // when
        PaymentResponse response = paymentService.processPayment(100L, request);

        // then
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getMenuIds()).containsExactly(10L);
        assertThat(response.getPaymentAmount()).isEqualByComparingTo(BigDecimal.valueOf(8000));
    }

    @Test
    @DisplayName("포인트 잔액 부족 시 INSUFFICIENT_POINT 예외를 던진다")
    void processPayment_throwsWhenInsufficientPoint() {
        // given
        Point lowPoint = Point.createPoint(user);
        lowPoint.charge(BigDecimal.valueOf(1000));
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(8000));

        when(orderRepository.findByIdWithLock(100L)).thenReturn(Optional.of(order));
        when(pointRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(lowPoint));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(100L, request))
                .isInstanceOf(ServiceException.class)
                .satisfies(e -> assertThat(((ServiceException) e).getErrorCode())
                        .isEqualTo(ErrorCode.INSUFFICIENT_POINT));
    }

    @Test
    @DisplayName("결제 금액과 주문 금액이 다르면 INVALID_PAYMENT_AMOUNT 예외를 던진다")
    void processPayment_throwsWhenAmountMismatch() {
        // given
        PaymentRequest request = new PaymentRequest(BigDecimal.valueOf(9999));
        when(orderRepository.findByIdWithLock(100L)).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(100L, request))
                .isInstanceOf(ServiceException.class)
                .satisfies(e -> assertThat(((ServiceException) e).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_PAYMENT_AMOUNT));
    }
}
