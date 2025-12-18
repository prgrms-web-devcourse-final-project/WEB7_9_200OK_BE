package com.windfall.api.auction.dto.response.message;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경매 실시간 접속자 응답 DTO")
public record AuctionViewerMessage (
  Long auctionId,
  long viewerCount
) {
  public static AuctionViewerMessage of(Long auctionId, long viewerCount) {
    return new AuctionViewerMessage(auctionId, viewerCount);
  }
}
