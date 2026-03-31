package com.cafeorder.domain.point.service;

import com.cafeorder.domain.point.dto.AddPointsRequest;
import com.cafeorder.domain.point.entity.Point;
import com.cafeorder.domain.point.entity.PointHistory;
import com.cafeorder.domain.point.repository.PointHistoryRepository;
import com.cafeorder.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void addPoints(Long userId, AddPointsRequest request) {

        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 포인트 지갑을 찾을 수 없습니다."));

        point.charge(request.getAmount());

        PointHistory history = PointHistory.create(
                point,
                PointHistory.TransactionType.CHARGE,
                request.getAmount()
        );
        pointHistoryRepository.save(history);
    }
}
