package org.sparta.springtask.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.sparta.springtask.common.code.ResponseCode;
import org.springframework.http.HttpStatus;

@Slf4j
public class ForbiddenException extends ApiException {
    public ForbiddenException(ResponseCode responseCode) {
        super(HttpStatus.NOT_FOUND, responseCode.getMessage());

        log.error("[{}] {}", HttpStatus.NOT_FOUND, responseCode.getMessage());
    }
}