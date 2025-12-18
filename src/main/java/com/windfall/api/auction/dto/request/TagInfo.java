package com.windfall.api.auction.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "태그 등록 요청 DTO")
public record TagInfo(

    @Size(max = 10, message = "태그 최대 글자 수를 초과했습니다.")
    @NotEmpty(message = "태그에 빈 문자를 등록할 수 없습니다.")
    @Pattern(regexp = "^[^\\s]+$", message = "공백을 포함할 수 없습니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "한글, 영어, 숫자만 입력 가능합니다.")
    @Schema(description = "단일 경매 태그")
    String name
) {
    public static TagInfo from(String name) {
        return new TagInfo(name);
    }
}