package com.windfall.api.mypage.controller;

import com.windfall.api.mypage.dto.auctionlikelist.BaseAuctionLikeList;
import com.windfall.api.mypage.dto.notificationsetlist.BaseNotificationSetList;
import com.windfall.api.mypage.dto.purchasehistory.BasePurchaseHistory;
import com.windfall.api.mypage.dto.recentviewlist.BaseRecentViewList;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.notification.enums.NotificationType;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "MyPage", description = "마이페이지 API")
public interface MyPageSpecification {

  @Operation(summary = "나의 구매내역 조회", description = "자신의 구매내역을 조회합니다.")
  ApiResponse<SliceResponse<BasePurchaseHistory>> getMyPurchaseHistory(@PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) String filter,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

  @Operation(summary = "나의 알림내역 조회", description = "자신이 알림설정한 경매 목록을 조회합니다.")
  ApiResponse<SliceResponse<BaseNotificationSetList>> getMyNotifications(@PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) AuctionStatus filter,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

  @Operation(summary = "나의 찜 목록 조회", description = "자신이 찜한 경매 목록을 조회합니다.")
  ApiResponse<SliceResponse<BaseAuctionLikeList>> getMyAuctionLikes(@PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) AuctionStatus filter,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

  @Operation(summary = "나의 최근 본 내역 조회", description = "자신이 최근에 본 경매 목록을 조회합니다.")
  ApiResponse<SliceResponse<BaseRecentViewList>> getMyRecentViews(@PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) AuctionStatus filter,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );
}
