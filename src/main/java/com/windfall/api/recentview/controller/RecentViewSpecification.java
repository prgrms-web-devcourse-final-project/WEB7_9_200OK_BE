package com.windfall.api.recentview.controller;

import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "RecentView", description = "최근 본 경매 내역 API")
public interface RecentViewSpecification {

  @Operation(summary = "최근 본 경매내역", description = "최근 본 경매 내역을 기록합니다. (경매 상세 페이지에서 사용됩니다.)")
  ApiResponse<Void> recordRecentView(
      @PathVariable Long auctionId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );


}
