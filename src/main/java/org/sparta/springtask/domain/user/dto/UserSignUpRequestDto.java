package org.sparta.springtask.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public sealed interface UserSignUpRequestDto permits UserSignUpRequestDto.SignUpDto {
    record SignUpDto(
            @NotBlank(message = "이메일은 필수입니다.")
            @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
                    , message = "이메일 형식은 ~@~.~를 지켜주세요.")
            String email ,
            @NotBlank(message = "비밀번호는 필수입니다.")
            @Pattern(
                    regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                    message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해 최소 8자, 최대 20자 입력해주세요.")
            String password ,
            @NotBlank(message = "이름은 필수입니다.")
            String name ,
            @NotBlank(message = "권한 설정은 필수입니다.")
            String authority) implements UserSignUpRequestDto {}
}