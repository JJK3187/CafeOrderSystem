package com.cafeorder.domain.menu.controller;

import com.cafeorder.config.exception.ApiResponse;
import com.cafeorder.domain.menu.dto.MenuResponse;
import com.cafeorder.domain.menu.dto.PopularMenuResponse;
import com.cafeorder.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(menuService.getAllMenus()));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularMenuResponse>>> getPopular() {
        return ResponseEntity.ok(ApiResponse.success(menuService.getPopularMenus()));
    }
}
