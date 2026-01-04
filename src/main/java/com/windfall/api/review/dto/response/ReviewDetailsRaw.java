package com.windfall.api.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewDetailsRaw(

    @Schema(description = "경매 ID")
    Long auctionId,

    @Schema(description = "리뷰 ID")
    Long reviewId,

    @Schema(description = "판매자 ID")
    Long sellerId,

    @Schema(description = "경매 제목 (상품 이름)")
    String auctionTitle,

    @Schema(description = "판매자 이름")
    String nickname,

    @Schema(description = "별점")
    int rating,

    @Schema(description = "내용")
    String content

) {

}
