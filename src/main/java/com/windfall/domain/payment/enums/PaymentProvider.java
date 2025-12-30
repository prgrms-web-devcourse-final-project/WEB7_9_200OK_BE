package com.windfall.domain.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentProvider {
  TOSS("토스 결제 api");

  private final String description;
}
