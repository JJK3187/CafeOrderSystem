package com.cafeorder.domain.menu.dto;

import lombok.Getter;


@Getter
public class PopularMenuResponse {

    private final Long   menuId;
    private final String menuName;
    private final Long   orderCount;

    public PopularMenuResponse(Long menuId, String menuName, Long orderCount) {
        this.menuId     = menuId;
        this.menuName   = menuName;
        this.orderCount = orderCount;
    }
}

