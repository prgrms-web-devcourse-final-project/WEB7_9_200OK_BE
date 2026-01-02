package com.windfall.api.recentview.controller;

import com.windfall.api.recentview.service.RecentViewService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recentview")
@RequiredArgsConstructor
public class RecentViewController implements RecentViewSpecification {

  private final RecentViewService recentViewService;

  @Override
  @PostMapping("/{auctionId}")
  public ApiResponse<Void> recordRecentView(
      @PathVariable Long auctionId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){

    Long userId = userDetails.getUserId();
    recentViewService.record(auctionId, userId);

    return ApiResponse.ok("최근 본 목록이 업데이트 되었습니다.", null);
  }

}
