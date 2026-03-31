package com.cafeorder.domain.point.service;

import com.cafeorder.domain.point.dto.AddPointsRequest;
import com.cafeorder.domain.point.entity.Point;
import com.cafeorder.domain.point.entity.PointHistory;
import com.cafeorder.domain.point.repository.PointHistoryRepository;
import com.cafeorder.domain.point.repository.PointRepository;
import com.cafeorder.domain.users.entity.User;
import com.cafeorder.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void addPoints(Long userId, AddPointsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseGet(() -> createWalletThenLock(user));

        point.charge(request.getAmount());

        PointHistory history = PointHistory.create(
                point,
                PointHistory.TransactionType.CHARGE,
                request.getAmount()
        );
        pointHistoryRepository.save(history);
    }

    private Point createWalletThenLock(User user) {
        try {
            pointRepository.saveAndFlush(Point.createPoint(user));
        } catch (DataIntegrityViolationException ignored) {
            // 동시 요청으로 이미 생성된 경우를 허용하고 잠금 재조회한다.
        }

        return pointRepository.findByUserIdWithLock(user.getId())
                .orElseThrow(() -> new IllegalStateException("포인트 지갑 생성/조회에 실패했습니다."));
    }
}
