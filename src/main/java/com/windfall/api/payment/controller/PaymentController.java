package com.windfall.api.payment.controller;

import com.windfall.api.payment.dto.request.PaymentConfirmRequest;
import com.windfall.api.payment.dto.response.PaymentConfirmResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

  @PostMapping(value = "/confirm")
  public ApiResponse<PaymentConfirmResponse> confirmPayment(
      @RequestBody PaymentConfirmRequest paymentConfirmRequest,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {

    PaymentConfirmResponse response = new PaymentConfirmResponse(
        1L, "orderId", "paymentKey", 999L, "MOBILE_PAYMENT", "DONE");

    return ApiResponse.ok("결제 승인 성공했습니다.", response);

  }

}
