package com.windfall.api.trade.controller;

import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.config.swagger.ApiErrorCodes;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_TRADE;
import static com.windfall.global.exception.ErrorCode.NOT_PAYMENT_COMPLETED_TRADE;
import static com.windfall.global.exception.ErrorCode.NOT_MATCHED_BUYER_ID;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Trade", description = "거래 API")
public interface TradeSpecification {

  @ApiErrorCodes({NOT_FOUND_TRADE, NOT_PAYMENT_COMPLETED_TRADE, NOT_MATCHED_BUYER_ID})
  ApiResponse<Void> purchaseConfirmTrade(
      @PathVariable Long tradeId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

}
