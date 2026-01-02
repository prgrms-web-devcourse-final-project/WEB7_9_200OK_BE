package com.windfall.api.mypage.controller;

import com.windfall.api.mypage.dto.auctionlikelist.AuctionLikeListResponse;
import com.windfall.api.mypage.dto.auctionlikelist.BaseAuctionLikeList;
import com.windfall.api.mypage.dto.notificationsetlist.BaseNotificationSetList;
import com.windfall.api.mypage.dto.purchasehistory.BasePurchaseHistory;
import com.windfall.api.mypage.dto.recentviewlist.BaseRecentViewList;
import com.windfall.api.mypage.service.AuctionLikeListService;
import com.windfall.api.mypage.service.NotificationSetListService;
import com.windfall.api.mypage.service.PurchaseHistoryService;
import com.windfall.api.mypage.service.RecentViewListService;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me")
public class MyPageController implements MyPageSpecification{

  private final PurchaseHistoryService purchaseHistoryService;
  private final NotificationSetListService notificationSetListService;
  private final AuctionLikeListService auctionLikeListService;
  private final RecentViewListService recentViewListService;

  @GetMapping("/purchases")
  public ApiResponse<SliceResponse<BasePurchaseHistory>> getMyPurchaseHistory(
      @PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) String filter,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){
    Long userId = userDetails.getUserId();
    SliceResponse<BasePurchaseHistory> response = purchaseHistoryService.getPurchaseHistories(userId, filter, pageable);

    return ApiResponse.ok("구매내역 조회에 성공하였습니다.", response);

  }

  @Override
  @GetMapping("/notifications")
  public ApiResponse<SliceResponse<BaseNotificationSetList>> getMyNotifications(
      @PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) AuctionStatus filter,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = userDetails.getUserId();
    SliceResponse<BaseNotificationSetList> response = notificationSetListService.getMyNotifications(userId, filter, pageable);
    return ApiResponse.ok("알림내역 조회에 성공하였습니다.", response);
  }

  @Override
  @GetMapping("/likes")
  public ApiResponse<SliceResponse<BaseAuctionLikeList>> getMyAuctionLikes(
      @PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) AuctionStatus filter,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = userDetails.getUserId();
    SliceResponse<BaseAuctionLikeList> response = auctionLikeListService.getMyAuctionLikes(userId, filter, pageable);

    return ApiResponse.ok("찜 목록 조회에 성공하였습니다.", response);
  }

  @Override
  @GetMapping("/recentviews")
  public ApiResponse<SliceResponse<BaseRecentViewList>> getMyRecentViews(
      @PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) AuctionStatus filter,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = userDetails.getUserId();
    SliceResponse<BaseRecentViewList> response = recentViewListService.getMyRecentViewLists(userId, filter, pageable);

    return ApiResponse.ok("최근 본 경매 내역 조회에 성공하였습니다.", response);
  }
}
