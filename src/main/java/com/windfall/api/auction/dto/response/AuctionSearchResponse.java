package com.windfall.api.auction.dto.response;

import com.windfall.api.like.dto.response.AuctionLikeSupport;
import com.windfall.domain.auction.enums.AuctionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "경매 검색 응답 DTO")
public record AuctionSearchResponse(

    @Schema(description = "경매 ID")
    Long auctionId,

    @Schema(description = "경매 이미지(썸네일)")
    String imageUrl,

    @Schema(description = "경매 제목")
    String title,

    @Schema(description = "경매 시작가")
    Long startPrice,

    @Schema(description = "경매 현재가")
    Long currentPrice,

    @Schema(description = "경매 하락율")
    Long discountRate,

    @Schema(description = "경매 찜 여부")
    Boolean isLiked,

    @Schema(description = "경매 시작일")
    LocalDateTime startedAt,

    @Schema(description = "경매 상태 (경매 예정, 경매 진행 중, 경매 종료)")
    AuctionStatus status,
    @Schema(description = "경매 시작 알림 여부")
    Boolean isNotification
) implements AuctionLikeSupport<AuctionSearchResponse> {

  @Override
  public AuctionSearchResponse withIsLiked(boolean liked) {
    return new AuctionSearchResponse(
        this.auctionId,
        this.imageUrl,
        this.title,
        this.startPrice,
        this.currentPrice,
        this.discountRate,
        liked,
        this.startedAt,
        this.status,
        this.isNotification
    );
  }
}
