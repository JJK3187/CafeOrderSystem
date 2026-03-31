package com.cafeorder.domain.point.controller;

import com.cafeorder.domain.point.dto.AddPointsRequest;
import com.cafeorder.domain.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/add")
    public ResponseEntity<Void> addPoints(
            @RequestParam Long memberId,
            @Valid @RequestBody AddPointsRequest request
    ) {
        pointService.addPoints(memberId, request);
        return ResponseEntity.ok().build();
    }
}
