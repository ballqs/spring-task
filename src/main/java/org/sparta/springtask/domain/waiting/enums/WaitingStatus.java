package org.sparta.springtask.domain.waiting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sparta.springtask.common.code.ResponseCode;
import org.sparta.springtask.common.exception.InvalidParameterException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum WaitingStatus {
    WAITING(Status.WAITING),
    COMPLETED(Status.COMPLETED),
    CANCELED(Status.CANCELED);

    private final String waitingStatus;

    public static WaitingStatus of(String role) {
        return Arrays.stream(WaitingStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException(ResponseCode.INVALID_WAITING_STATUS));
    }

    public static class Status {

        public static final String WAITING = "대기 중";
        public static final String COMPLETED = "대기 완료";
        public static final String CANCELED = "대기 취소";
    }
}
