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
    "currentPrice",
    "discountPercent",
    "startedAt",
    "notificationInfo"
})
public class ProcessingNotificationSetListResponse extends BaseNotificationSetList {

  @Schema(description = "현재가")
  int currentPrice;

  @Schema(description = "하락 퍼센트")
  int discountPercent;

  @Builder
  public ProcessingNotificationSetListResponse(String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt, boolean alertStart,
      boolean alertEnd, boolean alertPrice, int triggerPrice, int currentPrice,
      int discountPercent) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt, alertStart, alertEnd,
        alertPrice, triggerPrice);
    this.currentPrice = currentPrice;
    this.discountPercent = discountPercent;
  }

  public static ProcessingNotificationSetListResponse from(Tuple tuple){

    return ProcessingNotificationSetListResponse.builder()
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .currentPrice(tuple.get("currentPrice", Long.class).intValue())
        .discountPercent(tuple.get("discountPercent", BigDecimal.class).intValue())
        .startedAt(tuple.get("startedAt", Date.class).toLocalDate())
        .alertStart(isAlertActive(tuple.get("alertStart", Long.class)))
        .alertEnd(isAlertActive(tuple.get("alertEnd", Long.class)))
        .alertPrice(isAlertActive(tuple.get("alertPrice", Long.class)))
        .triggerPrice(tuple.get("triggerPrice", Long.class).intValue())
        .build();
  }
}
