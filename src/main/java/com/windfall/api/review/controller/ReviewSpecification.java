package com.windfall.api.review.controller;

import static com.windfall.global.exception.ErrorCode.INVALID_S3_UPLOAD;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_REVIEW;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_TRADE;
import static com.windfall.global.exception.ErrorCode.NOT_MATCHED_BUYER_ID;
import static com.windfall.global.exception.ErrorCode.NOT_PURCHASE_CONFIRMED;
import static com.windfall.global.exception.ErrorCode.REVIEW_ALREADY_EXISTS;

import com.windfall.api.review.dto.request.CreateReviewRequest;
import com.windfall.api.review.dto.request.UpdateReviewRequest;
import com.windfall.api.review.dto.response.CreateReviewResponse;
import com.windfall.api.review.dto.response.ReviewDetailsResponse;
import com.windfall.api.review.dto.response.UpdateReviewResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.config.swagger.ApiErrorCodes;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Review", description = "리뷰 API")
public interface ReviewSpecification {

  @ApiErrorCodes({NOT_FOUND_TRADE, REVIEW_ALREADY_EXISTS, NOT_PURCHASE_CONFIRMED, NOT_MATCHED_BUYER_ID})
  @Operation(summary = "리뷰 생성", description = "리뷰를 생성합니다.")
  ApiResponse<CreateReviewResponse> createReview(
      @RequestBody @Valid CreateReviewRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

  @ApiErrorCodes({NOT_FOUND_REVIEW, NOT_MATCHED_BUYER_ID})
  @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다.")
  ApiResponse<UpdateReviewResponse> updateReview(
      @RequestBody @Valid UpdateReviewRequest request,
      @PathVariable Long reviewId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

  @ApiErrorCodes({NOT_FOUND_REVIEW, NOT_MATCHED_BUYER_ID})
  @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
  ApiResponse<Void> deleteReview(
      @PathVariable Long reviewId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

  @ApiErrorCodes({NOT_FOUND_REVIEW})
  @Operation(summary = "리뷰 상세 조회", description = "리뷰 상세를 조회합니다. (리뷰 수정 시 사용됩니다.)")
  ApiResponse<ReviewDetailsResponse> readReviewDetails(
      @PathVariable Long reviewId
  );
}
