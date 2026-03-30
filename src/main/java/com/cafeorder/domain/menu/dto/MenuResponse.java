package com.cafeorder.domain.menu.dto;

import com.cafeorder.domain.menu.entity.Menu;
import com.cafeorder.domain.menu.entity.MenuStatus;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MenuResponse {

    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final int stockQuantity;
    private final MenuStatus menuStatus;

    public MenuResponse(Long id, String name, BigDecimal price, int stockQuantity, MenuStatus menuStatus) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.menuStatus = menuStatus;
    }

    public static MenuResponse fromEntity(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getStockQuantity(),
                menu.getMenuStatus()
        );
    }
}
