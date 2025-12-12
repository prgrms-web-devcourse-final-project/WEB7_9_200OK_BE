package com.windfall.api.auction.dto.response.info;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "실시간 인기 랭킹 응답 DTO")
public record popularInfo(
    @Schema(description = "경매 ID")
    Long auctionId,

    @Schema(description = "상품 이미지 URL")
    String imageUrl,

    @Schema(description = "상품 제목")
    String title,

    @Schema(description = "시작가")
    int startPrice,

    @Schema(description = "현재가")
    int currentPrice,

    @Schema(description = "하락 퍼센트")
    double discountRate,

    @Schema(description = "사용자 찜 여부")
    boolean isLiked,

    @Schema(description = "경매 시작 시간")
    LocalDateTime startedAt

) {

}
