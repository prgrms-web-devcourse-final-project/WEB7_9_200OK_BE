package com.windfall.api.mypage.dto.notificationsetlist;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "status",
    "auctionId",
    "title",
    "auctionImageUrl",
    "startPrice",
    "endPrice",
    "discountPercent",
    "startedAt",
    "tradeStatus",
    "notificationInfo"
})
public class CompletedNotificationSetListResponse extends BaseNotificationSetList{

  @Schema(description = "하락 퍼센트")
  private final int discountPercent;

  @Schema(description = "낙찰가")
  private final int endPrice;

  @Schema(description = "거래 상태")
  private final String tradeStatus;

  @Builder
  public CompletedNotificationSetListResponse(String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt, boolean alertStart,
      boolean alertEnd, boolean alertPrice, int triggerPrice, int discountPercent, int endPrice,
      String tradeStatus) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt, alertStart, alertEnd,
        alertPrice, triggerPrice);
    this.discountPercent = discountPercent;
    this.endPrice = endPrice;
    this.tradeStatus = tradeStatus;
  }

  public static CompletedNotificationSetListResponse from(Tuple tuple){
    return CompletedNotificationSetListResponse.builder()
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .endPrice(tuple.get("endPrice", Long.class).intValue())
        .discountPercent(tuple.get("discountPercent", BigDecimal.class).intValue())
        .startedAt(tuple.get("startedAt", Date.class).toLocalDate())
        .alertStart(isAlertActive(tuple.get("alertStart", Long.class)))
        .alertEnd(isAlertActive(tuple.get("alertEnd", Long.class)))
        .alertPrice(isAlertActive(tuple.get("alertPrice", Long.class)))
        .triggerPrice(tuple.get("triggerPrice", Long.class).intValue())
        .tradeStatus(tuple.get("tradeStatus", String.class))
        .build();
  }
}
