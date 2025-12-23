package com.windfall.api.user.dto.response.saleshistory;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
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
    "currentPrice",
    "discountPercent",
    "startedAt",
    "tradeStatus",
}) //json 출력 순서 맞추기
public class ProcessingSalesHistoryResponse extends BaseSalesHistoryResponse {

  @Schema(description = "현재가")
  int currentPrice;

  @Schema(description = "하락 퍼센트")
  int discountPercent;

  @Builder
  public ProcessingSalesHistoryResponse(String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt, int currentPrice,
      int discountPercent) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt);
    this.currentPrice = currentPrice;
    this.discountPercent = discountPercent;
  }

  public static ProcessingSalesHistoryResponse from(Tuple tuple){
    return ProcessingSalesHistoryResponse
        .builder()
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .startedAt(tuple.get("startedAt", java.sql.Date.class).toLocalDate())
        .discountPercent(tuple.get("discountPercent", BigDecimal.class).intValue())
        .currentPrice(tuple.get("currentPrice", Long.class).intValue())
        .build();

  }

}
