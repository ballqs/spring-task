package org.sparta.springtask.common.dto;

import lombok.Getter;
import org.sparta.springtask.common.exception.ApiException;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse extends BaseResponse {
    LocalDateTime timestamp;

    private ErrorResponse(ApiException apiException) {
        super(apiException.getHttpStatus().value(), apiException.getMessage());
        this.timestamp = LocalDateTime.now();
    }

    private ErrorResponse(int code, String message) {
        super(code, message);
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse from(ApiException apiException) {
        return new ErrorResponse(apiException);
    }

    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message);
    }
}