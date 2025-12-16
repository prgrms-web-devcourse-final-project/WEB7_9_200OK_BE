package com.windfall.api.auction.dto.response;

import com.windfall.domain.auction.enums.AuctionStatus;
import java.time.LocalDateTime;

public record AuctionSearchResponse(
    Long auctionId,
    String imageUrl,
    String title,
    Long startPrice,
    Long currentPrice,
    Long discountRate,
    Boolean isLiked,
    LocalDateTime startedAt,
    AuctionStatus status
) {
}
