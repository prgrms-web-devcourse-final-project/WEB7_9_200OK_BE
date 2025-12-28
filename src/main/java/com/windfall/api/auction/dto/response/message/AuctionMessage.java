package com.windfall.api.auction.dto.response.message;

import com.windfall.domain.auction.enums.AuctionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경매 가격 하락 및 경매 상태 응답 DTO")
public record AuctionMessage(
    Long auctionId,
    long currentPrice,
    AuctionStatus status
) {

  public static AuctionMessage of(Long auctionId, long currentPrice, AuctionStatus status) {
    return new AuctionMessage(
        auctionId,
        currentPrice,
        status
    );
  }
}

