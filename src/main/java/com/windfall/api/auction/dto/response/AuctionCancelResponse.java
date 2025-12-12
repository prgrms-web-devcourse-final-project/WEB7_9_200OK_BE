package com.windfall.api.auction.dto.response;

import com.windfall.domain.auction.enums.AuctionStatus;

public record AuctionCancelResponse(
    Long auctionId,
    AuctionStatus status
) {

  public static AuctionCancelResponse of(Long auctionId, AuctionStatus auctionStatus) {
    return new AuctionCancelResponse(auctionId, auctionStatus);
  }
}
