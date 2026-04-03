package com.cafeorder.domain.menu.dto;

import lombok.Getter;

/**
 * 인기 메뉴 응답 DTO
 * - JPQL 생성자 표현식(new ... )으로 직접 매핑되므로
 *   생성자 파라미터 순서(menuId, menuName, orderCount)를 변경하면 안 됩니다.
 */
@Getter
public class PopularMenuResponse {

    private final Long   menuId;
    private final String menuName;
    private final Long   orderCount;

    /** JPQL 생성자 표현식 전용 — 파라미터 순서 고정 */
    public PopularMenuResponse(Long menuId, String menuName, Long orderCount) {
        this.menuId     = menuId;
        this.menuName   = menuName;
        this.orderCount = orderCount;
    }
}

