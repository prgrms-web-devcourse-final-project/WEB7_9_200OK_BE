package com.windfall.api.auction.dto.response.info;

import com.windfall.api.like.dto.response.AuctionLikeSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "경매 진행 중 응답 DTO")
public record ProcessInfo(
    @Schema(description = "경매 ID")
    Long auctionId,

    @Schema(description = "상품 이미지 URL 목록")
    String imageUrl,

    @Schema(description = "상품 제목")
    String title,

    @Schema(description = "시작가")
    Long startPrice,

    @Schema(description = "현재가")
    Long currentPrice,

    @Schema(description = "하락 퍼센트")
    Long discountRate,

    @Schema(description = "사용자 찜 여부")
    Boolean isLiked,

    @Schema(description = "경매 시작 시간")
    LocalDateTime startedAt
) implements AuctionLikeSupport<ProcessInfo> {

  @Override
  public ProcessInfo withIsLiked(boolean isLiked) {
    return new ProcessInfo(
        auctionId,
        imageUrl,
        title,
        startPrice,
        currentPrice,
        discountRate,
        isLiked,
        startedAt
    );
  }
}
