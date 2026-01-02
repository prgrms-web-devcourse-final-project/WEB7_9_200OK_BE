package com.windfall.api.mypage.dto.dashboard;

import com.windfall.api.mypage.dto.recentviewlist.ProcessingRecentViewListResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProcessingDashBoardDetailsResponse extends BaseDashBoardDetails {

  @Schema(description = "현재가")
  int currentPrice;

  @Schema(description = "하락 퍼센트")
  int discountPercent;

  @Builder
  public ProcessingDashBoardDetailsResponse(String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt, int currentPrice,
      int discountPercent) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt);
    this.currentPrice = currentPrice;
    this.discountPercent = discountPercent;
  }

  public static ProcessingDashBoardDetailsResponse from(Tuple tuple){
    return ProcessingDashBoardDetailsResponse.builder()
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
