package com.windfall.api.mypage.dto.recentviewlist;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(
    subTypes = { // 자식 클래스
        RecentViewListResponse.class,
        CompletedRecentViewListResponse.class,
        ProcessingRecentViewListResponse.class
    }
)
public abstract class BaseRecentViewList {

  @Schema(description = "최근 본 내역 id")
  private final Long recentViewId;

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

  public BaseRecentViewList(Long recentViewId, String status, Long auctionId, String title,
      String auctionImageUrl, int startPrice, LocalDate startedAt) {
    this.recentViewId = recentViewId;
    this.status = status;
    this.auctionId = auctionId;
    this.title = title;
    this.auctionImageUrl = auctionImageUrl;
    this.startPrice = startPrice;
    this.startedAt = startedAt;
  }
}
