package com.cafeorder.domain.menu.entity;

import lombok.Getter;

@Getter
public enum MenuStatus {
    ON_SALE("ON_SALE", "판매중"),
    SOLD_OUT("SOLD_OUT", "품절");

    private final String code;
    private final String description;
    MenuStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
