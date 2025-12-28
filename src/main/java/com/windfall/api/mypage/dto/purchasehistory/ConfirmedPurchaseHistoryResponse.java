package com.windfall.api.mypage.dto.purchasehistory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConfirmedPurchaseHistoryResponse extends BasePurchaseHistory{

  @Schema(description = "리뷰 Id (리뷰가 없을경우 0으로 반환)")
  private final Long reviewId;

  @Builder
  public ConfirmedPurchaseHistoryResponse(String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, int endPrice, int discountPercent,
      LocalDate purchasedDate, Long roomId, int unreadCount, Long reviewId) {
    super(status, auctionId, title, auctionImageUrl, startPrice, endPrice, discountPercent,
        purchasedDate, roomId, unreadCount);
    this.reviewId = reviewId;
  }

  public static ConfirmedPurchaseHistoryResponse from(Tuple tuple){
    return ConfirmedPurchaseHistoryResponse
        .builder()
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .endPrice(tuple.get("endPrice", Long.class).intValue())
        .discountPercent(tuple.get("discountPercent", BigDecimal.class).intValue())
        .purchasedDate(tuple.get("purchasedDate", Date.class).toLocalDate())
        .roomId(tuple.get("roomId", Long.class))
        .unreadCount(tuple.get("unreadCount", BigDecimal.class).intValue())
        .reviewId(tuple.get("reviewId", Long.class))
        .build();
  }

}
