package com.windfall.api.chat.dto.response.info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경매 상품 정보 DTO")
public record AuctionInfo(

    @Schema(description = "경매 ID")
    Long auctionId,
    @Schema(description = "경매 상품명")
    String title,
    @Schema(description = "경매 상품 이미지 URL (현재는 null 또는 아무 값 대입 예정)")
    String imageUrl
) {}
