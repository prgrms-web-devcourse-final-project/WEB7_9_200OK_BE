package com.windfall.api.review.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public record UpdateReviewRequest(
    @Range(min=1, max=5, message = "별점은 최소 1점 최대 5점까지 주어져야 합니다.")
    Integer rating,

    @Length(max = 500, message = "리뷰 내용은 최대 500자 까지만 입력이 가능합니다.")
    String content
) {
}
