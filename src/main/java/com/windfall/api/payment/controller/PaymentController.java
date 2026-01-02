package com.windfall.api.payment.controller;

import com.windfall.api.payment.dto.request.PaymentConfirmRequest;
import com.windfall.api.payment.dto.response.PaymentConfirmResponse;
import com.windfall.api.payment.service.PaymentService;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping(value = "/confirm")
  public ApiResponse<PaymentConfirmResponse> confirmPayment(
      @RequestBody PaymentConfirmRequest paymentConfirmRequest) {

    PaymentConfirmResponse response = paymentService.confirmPayment(paymentConfirmRequest);
    return ApiResponse.ok("결제 승인 성공했습니다.", response);

  }

}
