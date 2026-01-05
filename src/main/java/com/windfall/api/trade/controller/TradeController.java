package com.windfall.api.trade.controller;

import com.windfall.api.trade.service.TradeService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class TradeController implements TradeSpecification{

  private final TradeService tradeService;

  @Override
  @PatchMapping("/{tradeId}/confirm")
  public ApiResponse<Void> purchaseConfirmTrade(
      @PathVariable Long tradeId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){
    Long userId = userDetails.getUserId();

    tradeService.purchaseConfirmTrade(userId, tradeId);

    return ApiResponse.ok("해당 경매상품의 구매를 확정하였습니다.", null);
  }

}
