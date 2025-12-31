package com.windfall.api.mypage.dto.auctionlikelist;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(
    subTypes = { // 자식 클래스
        AuctionLikeListResponse.class,
        CompletedAuctionLikeListResponse.class,
        ProcessingAuctionLikeListResponse.class
    }
)
public abstract class BaseAuctionLikeList {

  @Schema(description = "좋아요 ID")
  private final Long likeId;

  @Schema(description = "경매 상태")
  private final String status;

  @Schema(description = "경매 id")
  private final Long auctionId;

  @Schema(description = "경매 제목")
  private final String title;

  @Schema(description = "경매 이미지 url")
  private final String auctionImageUrl;

  @Schema(description = "경매 시작가")
  private final int startPrice;

  @Schema(description = "경매 시작일")
  private final LocalDate startedAt;

  public BaseAuctionLikeList(Long likeId, String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt) {
    this.likeId = likeId;
    this.status = status;
    this.auctionId = auctionId;
    this.title = title;
    this.auctionImageUrl = auctionImageUrl;
    this.startPrice = startPrice;
    this.startedAt = startedAt;
  }
}
