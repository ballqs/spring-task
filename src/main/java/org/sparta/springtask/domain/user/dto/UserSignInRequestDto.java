package org.sparta.springtask.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public sealed interface UserSignInRequestDto permits UserSignInRequestDto.SignInDto {
    record SignInDto(
            @NotBlank(message = "이메일은 필수입니다.")
            String email ,
            @NotBlank(message = "비밀번호는 필수입니다.")
            String password) implements UserSignInRequestDto {}
}