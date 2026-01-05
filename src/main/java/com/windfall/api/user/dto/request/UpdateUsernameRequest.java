package com.windfall.api.user.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record UpdateUsernameRequest(
    @NotBlank(message = "이름은 필수 항목입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*$", message = "영어, 숫자, 한글만 입력이 허용됩니다.")
    @Length(min=2, max=20, message = "이름은 최소 2글자 최대 20글자만 입력이 가능합니다.")
    String username
) {

}
