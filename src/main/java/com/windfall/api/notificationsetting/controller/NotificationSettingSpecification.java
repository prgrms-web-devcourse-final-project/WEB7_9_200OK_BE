package com.windfall.api.notificationsetting.controller;

import static com.windfall.global.exception.ErrorCode.INVALID_PRICE_NOTIFICATION;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_AUCTION;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_PRICE_REACHED_NOTY;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_USER;

import com.windfall.api.notificationsetting.dto.request.UpdateNotySettingRequest;
import com.windfall.api.notificationsetting.dto.response.ReadNotySettingResponse;
import com.windfall.api.notificationsetting.dto.response.UpdateNotySettingResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.config.swagger.ApiErrorCodes;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Notification Setting", description = "알림 세팅 API")
public interface NotificationSettingSpecification {

  @Operation(summary = "알림 세팅 조회", description = "알림 세팅을 조회합니다.")
  ApiResponse<ReadNotySettingResponse> read(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId,

      @Parameter(description = "사용자 ID", example = "1")
      @AuthenticationPrincipal CustomUserDetails user
  );

  @ApiErrorCodes({NOT_FOUND_USER, NOT_FOUND_AUCTION, INVALID_PRICE_NOTIFICATION, NOT_FOUND_PRICE_REACHED_NOTY})
  @Operation(summary = "알림 세팅 수정", description = "알림 세팅을 수정합니다.")
  ApiResponse<UpdateNotySettingResponse> update(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId,

      @Parameter(description = "알림 여부", required = true,
          example =
              """
              {
                "auctionStart": true,
                "auctionEnd": true,
                "priceReached": true,
                "price": 10000
              }
              """
      )
      @RequestBody UpdateNotySettingRequest request,

      @Parameter(description = "사용자 ID", example = "1")
      @AuthenticationPrincipal CustomUserDetails user
  );
}