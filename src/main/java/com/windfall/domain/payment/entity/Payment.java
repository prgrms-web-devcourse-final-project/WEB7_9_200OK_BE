package com.windfall.domain.payment.entity;

import com.windfall.domain.payment.enums.PaymentStatus;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더용
@Builder
public class Payment extends BaseEntity {

  // 결제 객체 먼저 생성 후, 결제 api 호출한 다음 paymentKey 값 추가.
  @Column(nullable = true, unique = true)
  private String paymentKey;

  @Column(nullable = false)
  private PaymentStatus status;

  @Embedded
  private PaymentSelection paymentSelection;

  @Column(nullable = false)
  private Long tradeId;

  @Column(nullable = false)
  private Long price;

  /** 결제 승인 완료 */
  public static com.windfall.domain.payment.entity.Payment confirm(
      Long tradeId,
      String paymentKey,
      Long price,
      PaymentSelection selection
  ) {
    return com.windfall.domain.payment.entity.Payment.builder()
        .tradeId(tradeId)
        .paymentKey(paymentKey)
        .price(price)
        .status(PaymentStatus.DONE)
        .paymentSelection(selection)
        .build();
  }
}
