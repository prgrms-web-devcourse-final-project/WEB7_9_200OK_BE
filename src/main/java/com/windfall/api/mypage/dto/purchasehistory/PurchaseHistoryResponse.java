package com.windfall.api.mypage.dto.purchasehistory;

import com.windfall.api.user.dto.response.saleshistory.ProcessingSalesHistoryResponse;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PurchaseHistoryResponse extends BasePurchaseHistory{

  @Builder
  public PurchaseHistoryResponse(String status, Long auctionId, String title,
      String auctionImageUrl,
      int startPrice, int endPrice, int discountPercent, LocalDate purchasedDate, Long roomId,
      int unreadCount) {
    super(status, auctionId, title, auctionImageUrl, startPrice, endPrice, discountPercent,
        purchasedDate, roomId, unreadCount);
  }

  public static PurchaseHistoryResponse from(Tuple tuple){
    return PurchaseHistoryResponse
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
        .build();
  }
}
