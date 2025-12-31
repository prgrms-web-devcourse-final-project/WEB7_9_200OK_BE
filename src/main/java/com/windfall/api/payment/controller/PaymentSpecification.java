package com.windfall.api.payment.controller;

import com.windfall.api.payment.dto.request.PaymentConfirmRequest;
import com.windfall.api.payment.dto.response.PaymentConfirmResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface PaymentSpecification {
  @Operation(summary = "결제 승인 요청", description = "결제 승인 성공/실패 시 값 반환합니다.")
  ApiResponse<PaymentConfirmResponse> confirmPayment(
      PaymentConfirmRequest paymentConfirmRequest,
      @AuthenticationPrincipal CustomUserDetails customUserDetails);
}
