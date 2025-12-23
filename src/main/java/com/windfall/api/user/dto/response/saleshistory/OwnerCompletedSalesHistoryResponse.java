package com.windfall.api.user.dto.response.saleshistory;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.windfall.api.chat.dto.response.info.ChatInfo;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
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
    "chatInfo"
}) //json 출력 순서 맞추기
public class OwnerCompletedSalesHistoryResponse extends BaseSalesHistoryResponse {

  private final int discountPercent;
  private final int endPrice;
  private final String tradeStatus;
  private final ChatInfo chatInfo;

  @Builder
  public OwnerCompletedSalesHistoryResponse(String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt, int discountPercent,
      int endPrice, String tradeStatus, Long roomId, int unreadCount) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt);
    this.discountPercent = discountPercent;
    this.endPrice = endPrice;
    this.tradeStatus = tradeStatus;
    this.chatInfo = new ChatInfo(roomId, unreadCount);
  }

  public static OwnerCompletedSalesHistoryResponse from(Tuple tuple){
    return OwnerCompletedSalesHistoryResponse
        .builder()
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .startedAt(tuple.get("startedAt", java.sql.Date.class).toLocalDate())
        .discountPercent(tuple.get("discountPercent", BigDecimal.class).intValue())
        .endPrice(tuple.get("endPrice", Long.class).intValue())
        .tradeStatus(tuple.get("tradeStatus", String.class))
        .roomId(tuple.get("roomId", Long.class))
        .unreadCount(tuple.get("unreadCount", BigDecimal.class).intValue())
        .build();
  }

}
