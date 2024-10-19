package org.sparta.springtask.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    // 공통
    SUCCESS("정상 처리되었습니다."),
    INVALID_TIMEOUT("다시 시도해주세요."),
    FORBIDDEN("접근 권한이 없습니다."),

    // 사용자
    NOT_FOUND_USER("해당 사용자는 존재하지 않습니다."),
    INVALID_USER_AUTHORITY("해당 사용자 권한은 유효하지 않습니다."),
    DUPLICATE_EMAIL("이미 존재하는 이메일입니다."),
    WRONG_EMAIL_OR_PASSWORD("이메일 혹은 비밀번호가 일치하지 않습니다."),
    WRONG_PASSWORD("비밀번호가 일치하지 않습니다."),

    // 매니저
    MANAGER_ALREADY_EXISTS("이미 매니저가 존재합니다.");

    private final String message;
}
