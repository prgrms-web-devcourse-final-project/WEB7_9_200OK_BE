package com.windfall.api.like.controller;

import static com.windfall.global.exception.ErrorCode.NOT_FOUND_AUCTION;

import com.windfall.api.like.dto.response.AuctionLikeResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.config.swagger.ApiErrorCodes;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Auction Like", description = "경매 찜 API")
public interface AuctionLikeSpecification {

  @ApiErrorCodes(NOT_FOUND_AUCTION)
  @Operation(summary = "경매 찜 등록/해제", description = "경매 게시물을 찜 등록/해제할 수 있습니다.")
  ApiResponse<AuctionLikeResponse> toggleLike(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId,

      @Parameter(description = "사용자 ID", required = true, example = "1")
      @AuthenticationPrincipal CustomUserDetails userId
  );
}