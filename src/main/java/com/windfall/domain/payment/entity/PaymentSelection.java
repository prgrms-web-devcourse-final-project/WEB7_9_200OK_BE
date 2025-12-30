//
// Payment에서 Value Object로 분리.
// Payment에서 유저가 선택한 결제 방법/수단이란 의미.
//
package com.windfall.domain.payment.entity;

import com.windfall.domain.payment.enums.PaymentMethod;
import com.windfall.domain.payment.enums.PaymentProvider;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@Getter
public class PaymentSelection {
  private PaymentProvider paymentProvider;
  private PaymentMethod paymentMethod;

  public PaymentSelection(PaymentProvider paymentProvider, PaymentMethod paymentMethod) {

    if (paymentProvider == null) throw new ErrorException(ErrorCode.INVALID_PAYMENT_PROVIDER);
    if (paymentMethod == null) throw new ErrorException(ErrorCode.INVALID_PAYMENT_METHOD);

    this.paymentProvider = paymentProvider;
    this.paymentMethod = paymentMethod;
  }
}
