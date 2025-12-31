package com.windfall.api.mypage.dto.auctionlikelist;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Tuple;
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
    "startedAt"
})
public class AuctionLikeListResponse extends BaseAuctionLikeList{

  @Builder
  public AuctionLikeListResponse(Long likeId, String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt) {
    super(likeId, status, auctionId, title, auctionImageUrl, startPrice, startedAt);
  }

  public static AuctionLikeListResponse from(Tuple tuple){
    return AuctionLikeListResponse.builder()
        .likeId(tuple.get("auctionLikeId", Long.class))
        .status(tuple.get("status", String.class))
        .auctionId(tuple.get("auctionId", Long.class))
        .title(tuple.get("title", String.class))
        .auctionImageUrl(tuple.get("auctionImageUrl", String.class))
        .startPrice(tuple.get("startPrice", Long.class).intValue())
        .startedAt(tuple.get("startedAt", Date.class).toLocalDate())
        .build();
  }

}
