package com.windfall.api.notification.controller;

import com.windfall.api.notification.dto.response.NotificationMarkAllResponse;
import com.windfall.api.notification.dto.response.NotificationMarkResponse;
import com.windfall.api.notification.dto.response.NotificationReadResponse;
import com.windfall.api.notification.service.NotificationService;
import com.windfall.api.notification.service.SseService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationSpecification{

  private final NotificationService notificationService;

  private final SseService sseService;

  @Override
  @GetMapping
  public ApiResponse<SliceResponse<NotificationReadResponse>> readNotification(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestParam @Min(value = 0, message = "page는 0부터 시작합니다.") int page,
      @RequestParam(defaultValue = "15") int size
  ) {
    PageRequest pageable = PageRequest.of(page, size, Sort.by(Direction.DESC,"createDate"));
    SliceResponse<NotificationReadResponse> response = notificationService.readNotification(user,pageable);
    return ApiResponse.ok("알림 조회에 성공했습니다.",response);
  }

  @Override
  @PatchMapping("/{notificationId}")
  public ApiResponse<NotificationMarkResponse> markAsRead(
      @PathVariable Long notificationId,
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    NotificationMarkResponse response = notificationService.markAsRead(notificationId,user);
    return ApiResponse.ok("알림이 읽음 처리되었습니다.",response);
  }

  @Override
  @PatchMapping
  public ApiResponse<NotificationMarkAllResponse> markAllAsRead(
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    NotificationMarkAllResponse response = notificationService.markAllAsRead(user);
    return ApiResponse.ok("알림이 모두 읽음 처리되었습니다.",response);
  }

  @Override
  @GetMapping(value = "/subscribe", produces = "text/event-stream")
  public SseEmitter subscribe(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId
  ){
    Long id = user.getUserId();
    return sseService.subscribe(id,lastEventId);
  }

}
