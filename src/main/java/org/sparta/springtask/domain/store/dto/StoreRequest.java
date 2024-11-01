package org.sparta.springtask.domain.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public sealed interface StoreRequest permits StoreRequest.Create {
    record Create(
            @NotBlank(message = "가게 이름은 필수입니다.")
            String name,
            @NotNull(message = "오픈 시간은 필수입니다.")
            LocalTime openTime,
            @NotNull(message = "닫는 시간은 필수입니다.")
            LocalTime closeTime,
            @NotNull(message = "테이블 수는 필수입니다.")
            Long tableCount,
            @NotBlank(message = "전화번호는 필수입니다.")
            String tel,
            @NotBlank(message = "우편번호는 필수입니다.")
            String zip,
            @NotBlank(message = "주소는 필수입니다.")
            String addr,
            @NotBlank(message = "주소상세는 필수입니다.")
            String addrDetail,
            String description
    ) implements StoreRequest {}
}
