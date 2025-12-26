package com.windfall.api.tag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "태그 자동 완성 관련 응답 DTO")
public record SearchTagInfo(

    @Schema(description = "태그 이름")
    String tagName,

    @Schema(description = "태그 ID")
    Long tagId,

    @Schema(description = "경매 ID")
    Long auctionId
) {
}