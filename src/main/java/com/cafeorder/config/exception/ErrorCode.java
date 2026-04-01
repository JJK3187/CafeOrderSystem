package com.cafeorder.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_001", "요청 값이 올바르지 않습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "해당 사용자를 찾을 수 없습니다."),

    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU_001", "존재하지 않는 메뉴가 포함되어 있습니다."),
    MENU_NOT_ON_SALE(HttpStatus.BAD_REQUEST, "MENU_002", "판매 중이 아닌 메뉴는 주문할 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "MENU_003", "메뉴 재고가 부족합니다."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_001", "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ORDER_002", "결제 가능한 주문 상태가 아닙니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "ORDER_003", "결제 금액이 주문 금액과 일치하지 않습니다."),

    POINT_WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "POINT_001", "사용자의 포인트를 찾을 수 없습니다."),
    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "POINT_002", "포인트 금액은 0보다 커야 합니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "POINT_003", "포인트 잔액이 부족합니다."),
    POINT_WALLET_INIT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "POINT_004", "포인트 지갑 생성/조회에 실패했습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_999", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}

