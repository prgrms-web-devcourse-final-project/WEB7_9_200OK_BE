package com.windfall.api.auction.dto.response.info;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "경매 예정 응답 DTO")
public record scheduledInfo(
    @Schema(description = "경매 ID")
    Long auctionId,

    @Schema(description = "상품 이미지 URL 목록")
    String imageUrl,

    @Schema(description = "상품 제목")
    String title,

    @Schema(description = "시작가")
    int startPrice,

    @Schema(description = "사용자 찜 여부")
    boolean isLiked,

    @Schema(description = "경매 시작 시간")
    LocalDateTime startedAt
) {

}
