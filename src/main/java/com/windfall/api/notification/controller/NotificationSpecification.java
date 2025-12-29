package com.windfall.api.notification.controller;

import static com.windfall.global.exception.ErrorCode.INVALID_DROP_AMOUNT;
import static com.windfall.global.exception.ErrorCode.INVALID_IMAGE_STATUS;
import static com.windfall.global.exception.ErrorCode.INVALID_STOP_LOSS;
import static com.windfall.global.exception.ErrorCode.INVALID_TIME;

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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Notification", description = "알림 API")
public interface NotificationSpecification {

  @ApiErrorCodes({INVALID_TIME, INVALID_STOP_LOSS, INVALID_DROP_AMOUNT,INVALID_IMAGE_STATUS})
  @Operation(summary = "알림 조회", description = "알림을 조회합니다.")
  ApiResponse<SliceResponse<NotificationReadResponse>> readNotification(
      @AuthenticationPrincipal CustomUserDetails user,

      @Parameter(description = "현재 페이지", example = "1")
      @RequestParam @Min(value = 0,message = "page는 0부터 시작합니다.") int page,

      @Parameter(description = "한 페이지 사이즈", example = "15")
      @RequestParam(defaultValue = "15") int size
  );
}
