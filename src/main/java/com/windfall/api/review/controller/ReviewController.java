package com.windfall.api.review.controller;

import com.windfall.api.review.dto.request.CreateReviewRequest;
import com.windfall.api.review.dto.request.UpdateReviewRequest;
import com.windfall.api.review.dto.response.CreateReviewResponse;
import com.windfall.api.review.dto.response.ReviewDetailsResponse;
import com.windfall.api.review.dto.response.UpdateReviewResponse;
import com.windfall.api.review.service.ReviewService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController implements ReviewSpecification{

  private final ReviewService reviewService;

  @PostMapping("")
  public ApiResponse<CreateReviewResponse> createReview(
      @RequestBody @Valid CreateReviewRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){

    Long userId = userDetails.getUserId();
    CreateReviewResponse response = reviewService.createReview(userId, request);

    return ApiResponse.created("리뷰가 생성되었습니다.", response);
  }

  @PatchMapping("/{reviewId}")
  public ApiResponse<UpdateReviewResponse> updateReview(
      @RequestBody @Valid UpdateReviewRequest request,
      @PathVariable Long reviewId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){

    Long userId = userDetails.getUserId();
    UpdateReviewResponse response = reviewService.updateReview(userId, reviewId, request);

    return ApiResponse.ok("리뷰가 수정되었습니다.", response);
  }

  @DeleteMapping("/{reviewId}")
  public ApiResponse<Void> deleteReview(
      @PathVariable Long reviewId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){

    Long userId = userDetails.getUserId();
    reviewService.deleteReview(userId, reviewId);

    return ApiResponse.ok("리뷰가 삭제되었습니다.", null);
  }

  @GetMapping("/{reviewId}")
  public ApiResponse<ReviewDetailsResponse> readReviewDetails(
      @PathVariable Long reviewId
  ){

    ReviewDetailsResponse response = reviewService.readReviewDetails(reviewId);

    return ApiResponse.ok("리뷰 조회에 성공하였습니다.", response);
  }

}
