package com.windfall.api.auction.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AuctionDetailResponse(
    Long auctionId,
    String title,
    String description,
    String category,
    List<String> imageUrls,

    SellerInfo seller,

    int startPrice,
    int currentPrice,
    int stopLoss,
    double discountRate,

    String status,
    int likeCount,
    boolean isLiked,
    long viewCount,
    LocalDateTime startedAt,

    List<AuctionHistoryResponse> recentPriceHistory

) {}

