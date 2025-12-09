package com.windfall.api.auction.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.user.entity.User;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record AuctionCreateResponse(
    Long sellerId,
    String title,
    String description,
    AuctionCategory category,
    Long startPrice,
    Long currentPrice,
    Long stopLoss,
    Long dropAmount,
    AuctionStatus status,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime startAt
) {

  public static AuctionCreateResponse from(Auction auction, Long sellerId) {
    return new AuctionCreateResponse(
        sellerId,
        auction.getTitle(),
        auction.getDescription(),
        auction.getCategory(),
        auction.getStartPrice(),
        auction.getCurrentPrice(),
        auction.getStopLoss(),
        auction.getDropAmount(),
        auction.getStatus(),
        auction.getStartedAt()
    );
  }
}
