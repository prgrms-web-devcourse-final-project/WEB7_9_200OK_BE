package com.windfall.api.mypage.dto.dashboard;

import jakarta.persistence.Tuple;
import java.sql.Date;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DashBoardDetailsResponse extends BaseDashBoardDetails{

  @Builder
  public DashBoardDetailsResponse(String status, Long auctionId, String title,
      String auctionImageUrl,
      int startPrice, LocalDate startedAt) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt);
  }

  public static DashBoardDetailsResponse from(Tuple tuple){
    return DashBoardDetailsResponse.builder()
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .startedAt(tuple.get("startedAt", Date.class).toLocalDate())
        .build();
  }
}
