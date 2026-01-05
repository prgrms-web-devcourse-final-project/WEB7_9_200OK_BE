package com.windfall.api.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReviewDetailsResponse(
    @Schema(description = "경매 ID")
    Long auctionId,

    @Schema(description = "리뷰 ID")
    Long reviewId,

    @Schema(description = "판매자 ID")
    Long sellerId,

    @Schema(description = "경매 이름 (상품 이름)")
    String auctionTitle,

    @Schema(description = "경매 썸네일 이미지")
    String auctionImageUrl,

    @Schema(description = "판매자 이름")
    String nickname,

    @Schema(description = "별점")
    double rating,

    @Schema(description = "내용")
    String content
  ){
  public static ReviewDetailsResponse of(ReviewDetailsRaw rawReview, String auctionImageUrl){
    return ReviewDetailsResponse.builder()
        .auctionId(rawReview.auctionId())
        .reviewId(rawReview.reviewId())
        .sellerId(rawReview.sellerId())
        .auctionImageUrl(auctionImageUrl)
        .auctionTitle(rawReview.auctionTitle())
        .content(rawReview.content())
        .rating(rawReview.rating())
        .nickname(rawReview.nickname())
        .build();
  }
}
