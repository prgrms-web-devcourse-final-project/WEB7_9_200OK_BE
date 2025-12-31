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
    "currentPrice",
    "discountPercent",
    "startedAt"
})
public class ProcessingAuctionLikeListResponse extends BaseAuctionLikeList{

  @Schema(description = "현재가")
  int currentPrice;

  @Schema(description = "하락 퍼센트")
  int discountPercent;

  @Builder
  public ProcessingAuctionLikeListResponse(Long likeId, String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt, int currentPrice,
      int discountPercent) {
    super(likeId, status, auctionId, title, auctionImageUrl, startPrice, startedAt);
    this.currentPrice = currentPrice;
    this.discountPercent = discountPercent;
  }

  public static ProcessingAuctionLikeListResponse from(Tuple tuple){
    return ProcessingAuctionLikeListResponse.builder()
        .likeId(tuple.get("auctionLikeId", Long.class))
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .currentPrice(tuple.get("currentPrice", Long.class).intValue())
        .discountPercent(tuple.get("discountPercent", BigDecimal.class).intValue())
        .startedAt(tuple.get("startedAt", Date.class).toLocalDate())
        .build();
  }

}
