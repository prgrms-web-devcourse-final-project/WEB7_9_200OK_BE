package com.windfall.api.auction.dto.response;

import java.time.LocalDateTime;

public record AuctionHistoryResponse(

    Long historyId,
    int currentPrice,
    double discountRate,
    int viewerCount,
    LocalDateTime createdAt

) {}