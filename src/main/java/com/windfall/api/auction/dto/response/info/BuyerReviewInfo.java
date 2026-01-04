package com.windfall.api.auction.dto.response.info;

public record BuyerReviewInfo(
    Long buyerId,
    String name,
    String content
) {
}
