package com.windfall.api.like.controller;

import com.windfall.api.like.dto.response.AuctionLikeResponse;
import com.windfall.api.like.service.AuctionLikeService;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/auction")
public class AuctionLikeController implements AuctionLikeSpecification{

  private final AuctionLikeService auctionLikeService;

  @Override
  @PostMapping("/{auctionId}/like")
  public ApiResponse<AuctionLikeResponse> toggleLike(
      @PathVariable Long auctionId,
      @RequestBody Long userId
  ) {
    AuctionLikeResponse response = auctionLikeService.toggleLike(auctionId, userId);
    return ApiResponse.ok("찜 토글을 성공했습니다.", response);
  }
}