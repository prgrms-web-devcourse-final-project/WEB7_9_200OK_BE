package com.windfall.api.user.dto.response.saleshistory;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
}) //json 출력 순서 맞추기
public class CompletedSalesHistoryResponse extends BaseSalesHistoryResponse {

  private final int discountPercent;
  private final int endPrice;
  private final String tradeStatus;

  @Builder
  public CompletedSalesHistoryResponse(String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt, int discountPercent,
      int endPrice, String tradeStatus) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt);
    this.discountPercent = discountPercent;
    this.endPrice = endPrice;
    this.tradeStatus = tradeStatus;
  }

  public static CompletedSalesHistoryResponse from(Tuple tuple){
    return CompletedSalesHistoryResponse
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
        .build();
  }

}
