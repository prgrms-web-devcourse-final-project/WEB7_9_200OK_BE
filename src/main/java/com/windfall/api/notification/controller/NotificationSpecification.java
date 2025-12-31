package com.windfall.api.notification.controller;

import static com.windfall.global.exception.ErrorCode.INVALID_NOTIFICATION;

import com.windfall.api.notification.dto.response.NotificationMarkAllResponse;
import com.windfall.api.notification.dto.response.NotificationMarkResponse;
import com.windfall.api.notification.dto.response.NotificationReadResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.config.swagger.ApiErrorCodes;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Notification", description = "알림 API")
public interface NotificationSpecification {

  @Operation(summary = "알림 조회", description = "알림을 조회합니다.")
  ApiResponse<SliceResponse<NotificationReadResponse>> readNotification(
      @AuthenticationPrincipal CustomUserDetails user,

      @Parameter(description = "현재 페이지", example = "1")
      @RequestParam @Min(value = 0,message = "page는 0부터 시작합니다.") int page,

      @Parameter(description = "한 페이지 사이즈", example = "15")
      @RequestParam(defaultValue = "15") int size
  );

  @ApiErrorCodes(INVALID_NOTIFICATION)
  @Operation(summary = "알림 단건 읽음 처리", description = "알림을 단건 읽음 처리합니다.")
  ApiResponse<NotificationMarkResponse> markAsRead(
      @PathVariable Long notificationId,
      @AuthenticationPrincipal CustomUserDetails user
  );

  @Operation(summary = "알림 다건 읽음 처리", description = "알림을 다건 읽음 처리합니다.")
  ApiResponse<NotificationMarkAllResponse> markAllAsRead(
      @AuthenticationPrincipal CustomUserDetails user
  );

  @Operation(summary = "실시간 알림 처리", description = "SSE를 이용해서 실시간 알림 처리를 합니다..")
  SseEmitter subscribe(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId
  );

}
