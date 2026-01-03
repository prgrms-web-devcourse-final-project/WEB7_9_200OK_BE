package com.windfall.api.notification.event.vo;

import java.time.LocalDateTime;

public record AuctionPriceDroppedEvent(
    Long auctionId,
    Long previousPrice,
    Long currentPrice,
    LocalDateTime droppedAt
) {}