package com.windfall.api.mypage.dto.notificationsetlist;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    "startedAt",
    "notificationInfo"
})
public class NotificationSetListResponse extends BaseNotificationSetList{

  @Builder
  public NotificationSetListResponse(String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt, boolean alertStart,
      boolean alertEnd, boolean alertPrice, int triggerPrice) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt, alertStart, alertEnd,
        alertPrice, triggerPrice);
  }

  public static NotificationSetListResponse from(Tuple tuple){
    return NotificationSetListResponse.builder()
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .startedAt(tuple.get("startedAt", Date.class).toLocalDate())
        .alertStart(isAlertActive(tuple.get("alertStart", Long.class)))
        .alertEnd(isAlertActive(tuple.get("alertEnd", Long.class)))
        .alertPrice(isAlertActive(tuple.get("alertPrice", Long.class)))
        .triggerPrice(tuple.get("triggerPrice", Long.class).intValue())
        .build();
  }

}
