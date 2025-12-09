package com.windfall.api.auction.dto.request;

import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record AuctionCreateRequest(
    Long sellerId,
    String title,
    String description,
    AuctionCategory category,
    Long startPrice,
    Long stopLoss,
    Long dropAmount,
    LocalDateTime startAt
) {
}
