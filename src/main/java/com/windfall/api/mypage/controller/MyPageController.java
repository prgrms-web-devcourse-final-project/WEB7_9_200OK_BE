package com.windfall.api.mypage.controller;

import com.windfall.api.mypage.dto.purchasehistory.BasePurchaseHistory;
import com.windfall.api.mypage.service.PurchaseHistoryService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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



}
