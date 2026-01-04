package com.windfall.api.auction.dto.response.stats;

import io.swagger.v3.oas.annotations.media.Schema;

public record SellerReviewStats(
    @Schema(description = "평균 별점")
    double rating,

    @Schema(description = "총 리뷰 수")
    int totalReviews
) {

}
