package com.windfall.api.mypage.dto.recentviewlist;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Tuple;
import java.sql.Date;
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
    "startedAt"
})
public class RecentViewListResponse extends BaseRecentViewList{

  @Builder
  public RecentViewListResponse(String status, Long auctionId, String title, String auctionImageUrl,
      int startPrice, LocalDate startedAt) {
    super(status, auctionId, title, auctionImageUrl, startPrice, startedAt);
  }

  public static RecentViewListResponse from(Tuple tuple){
    return RecentViewListResponse.builder()
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .startedAt(tuple.get("startedAt", Date.class).toLocalDate())
        .build();
  }
}
