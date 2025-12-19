package com.windfall.api.tag.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "태그 검색 요청 DTO")
public record SearchTagRequest (

    @Schema(description = "태그 검색어", example = "노트북")
    String keyword
) {
}