package io.hhplus.tdd.exception;

public class CustomException extends RuntimeException {
    private ErrorCode errorCode;

    public CustomException() {

    }

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
