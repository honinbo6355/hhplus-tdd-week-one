package io.hhplus.tdd.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "100", "입력값이 잘못되었습니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "101", "해당 유저가 존재하지 않습니다."),
    POINT_SHORTAGE(HttpStatus.BAD_REQUEST, "102", "사용할 포인트가 부족합니다.");

    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
