package com.windfall.api.user.dto.response.saleshistory;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Tuple;
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
}) //json 출력 순서 맞추기
public class SalesHistoryResponse extends BaseSalesHistoryResponse{

  @Builder
  public SalesHistoryResponse(String status, Long auctionId, String title, String auctionImageUrl,
      int startPrice, LocalDate startedAt) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt);
  }

  public static SalesHistoryResponse from(Tuple tuple){
    return SalesHistoryResponse.builder()
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .startedAt(tuple.get("startedAt", java.sql.Date.class).toLocalDate())
        .build();
  }
}
