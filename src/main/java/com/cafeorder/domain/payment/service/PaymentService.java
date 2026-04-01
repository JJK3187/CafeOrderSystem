package com.cafeorder.domain.payment.service;

import com.cafeorder.config.exception.ServiceException;
import com.cafeorder.config.exception.ErrorCode;
import com.cafeorder.domain.order.entity.Order;
import com.cafeorder.domain.order.entity.OrderStatus;
import com.cafeorder.domain.order.repository.OrderRepository;
import com.cafeorder.domain.payment.dto.PaymentRequest;
import com.cafeorder.domain.payment.dto.PaymentResponse;
import com.cafeorder.domain.payment.entity.Payment;
import com.cafeorder.domain.payment.repository.PaymentRepository;
import com.cafeorder.domain.point.entity.Point;
import com.cafeorder.domain.point.repository.PointRepository;
import com.cafeorder.domain.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;

    @Transactional
    public PaymentResponse processPayment(Long orderId, PaymentRequest request) {

        // 1. 주문 조회 및 유효성 검증
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new ServiceException(
                    ErrorCode.INVALID_ORDER_STATUS,
                    String.format("결제 대기 상태의 주문만 결제 가능합니다. (현재 상태: %s)",
                            order.getOrderStatus())
            );
        }

        User user = order.getUser();

        // 주문 금액과 요청 금액이 일치하는지 확인
        if (order.getTotalPrice().compareTo(request.getPaymentAmount()) != 0) {
            throw new ServiceException(
                    ErrorCode.INVALID_PAYMENT_AMOUNT,
                    String.format("결제 금액이 주문 금액과 일치하지 않습니다. (주문: %s, 요청: %s)",
                            order.getTotalPrice(), request.getPaymentAmount())
            );
        }

        // 2. 포인트 차감
        Point point = pointRepository.findByUserIdWithLock(user.getId())
                .orElseThrow(() -> new ServiceException(ErrorCode.POINT_WALLET_NOT_FOUND));

        try {
            point.use(request.getPaymentAmount());
        } catch (ServiceException e) {
            // 3. 결제 실패 -> Payment 기록 (FAILED)
            Payment failedPayment = Payment.createPendingPayment(
                    order,
                    user,
                    request.getPaymentAmount()
            );
            failedPayment.fail(e.getMessage());
            paymentRepository.save(failedPayment);

            // 주문 상태도 FAILED로 업데이트
            order.fail();
            orderRepository.save(order);

            throw e;
        }

        // 3. 결제 성공 -> Payment 기록 생성 (COMPLETED)
        Payment payment = Payment.createPendingPayment(
                order,
                user,
                request.getPaymentAmount()
        );
        payment.complete();
        Payment savedPayment = paymentRepository.save(payment);

        // 4. 주문 상태 완료 처리
        order.complete();
        orderRepository.save(order);

        return PaymentResponse.from(savedPayment);
    }
}
