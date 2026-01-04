package com.windfall.api.notificationsetting.controller;

import com.windfall.api.notificationsetting.dto.request.UpdateNotySettingRequest;
import com.windfall.api.notificationsetting.dto.response.ReadNotySettingResponse;
import com.windfall.api.notificationsetting.dto.response.UpdateNotySettingResponse;
import com.windfall.api.notificationsetting.service.NotificationSettingService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auctions/{auctionId}/notification-settings")
@RequiredArgsConstructor
public class NotificationSettingController implements NotificationSettingSpecification {

  private final NotificationSettingService notificationSettingService;

  @Override
  @GetMapping
  public ApiResponse<ReadNotySettingResponse> read(
      @PathVariable Long auctionId,
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    Long userId = null;
    if (user != null) {
      userId = user.getUserId();
    }

    ReadNotySettingResponse response = notificationSettingService.read(auctionId, userId);
    return ApiResponse.ok("알림 세팅 조회를 성공했습니다.", response);
  }

  @Override
  @PutMapping
  public ApiResponse<UpdateNotySettingResponse> update(
      @PathVariable Long auctionId,
      @RequestBody UpdateNotySettingRequest request,
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    UpdateNotySettingResponse response = notificationSettingService
        .update(auctionId, request, user.getUserId());
    return ApiResponse.ok("알림 세팅 수정을 성공했습니다.", response);
  }
}