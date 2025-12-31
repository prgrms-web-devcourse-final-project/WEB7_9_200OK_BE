package com.windfall.api.like.controller;

import static com.windfall.global.exception.ErrorCode.INVALID_TOKEN;

import com.windfall.api.like.dto.response.AuctionLikeResponse;
import com.windfall.api.like.service.AuctionLikeService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.exception.ErrorException;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auctions")
public class AuctionLikeController implements AuctionLikeSpecification {

  private final AuctionLikeService auctionLikeService;

  @Override
  @PostMapping("/{auctionId}/like")
  public ApiResponse<AuctionLikeResponse> toggleLike(
      @PathVariable Long auctionId,
      @AuthenticationPrincipal CustomUserDetails user
  ) {

    // 프론트 요청으로 추가(제거 예정)
    if (user == null) {
      throw new ErrorException(INVALID_TOKEN);
    }

    AuctionLikeResponse response = auctionLikeService.toggleLike(auctionId, user.getUserId());
    return ApiResponse.ok("찜 토글을 성공했습니다.", response);
  }
}