package com.windfall.api.review.dto.response;

import com.windfall.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CreateReviewResponse(
    @Schema(description = "리뷰 ID")
    Long reviewId,

    @Schema(description = "별점")
    double rating,

    @Schema(description = "내용")
    String content
) {
  public static CreateReviewResponse from(Review review){
    return CreateReviewResponse.builder()
        .reviewId(review.getId())
        .rating(review.getRating() / 10.0)
        .content(review.getContent())
        .build();
  }
}
