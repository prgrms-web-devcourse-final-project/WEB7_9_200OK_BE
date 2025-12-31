package com.windfall.api.mypage.dto.auctionlikelist;

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
    "likeId",
    "status",
    "auctionId",
    "title",
    "auctionImageUrl",
    "startPrice",
    "endPrice",
    "discountPercent",
    "startedAt",
    "tradeStatus"
})
public class CompletedAuctionLikeListResponse extends BaseAuctionLikeList{
  @Schema(description = "하락 퍼센트")
  private final int discountPercent;

  @Schema(description = "낙찰가")
  private final int endPrice;

  @Schema(description = "거래 상태")
  private final String tradeStatus;

  @Builder
  public CompletedAuctionLikeListResponse(Long likeId, String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt, int discountPercent,
      int endPrice,
      String tradeStatus) {
    super(likeId, status, auctionId, title, auctionImageUrl, startPrice, startedAt);
    this.discountPercent = discountPercent;
    this.endPrice = endPrice;
    this.tradeStatus = tradeStatus;
  }

  public static CompletedAuctionLikeListResponse from(Tuple tuple){
    return CompletedAuctionLikeListResponse.builder()
        .likeId(tuple.get("auctionLikeId", Long.class))
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .endPrice(tuple.get("endPrice", Long.class).intValue())
        .discountPercent(tuple.get("discountPercent", BigDecimal.class).intValue())
        .startedAt(tuple.get("startedAt", Date.class).toLocalDate())
        .tradeStatus(tuple.get("tradeStatus", String.class))
        .build();
  }
}

