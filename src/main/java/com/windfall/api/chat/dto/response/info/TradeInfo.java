package com.windfall.api.chat.dto.response.info;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "거래 정보 DTO")
public record TradeInfo(
    @Schema(description = "최종 구매가")
    Long finalPrice,

    @Schema(description = "구매일(결제 완료/구매확정 시점)")
    LocalDateTime purchasedAt

) {
  public static TradeInfo of(Long finalPrice, LocalDateTime purchasedAt) {
    return new TradeInfo(finalPrice, purchasedAt);
  }
}
