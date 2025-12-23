package com.windfall.api.user.dto.response.saleshistory;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public abstract class BaseSalesHistoryResponse {
  private final String status;
  private final Long auctionId;
  private final String title;
  private final String auctionImageUrl;
  private final int startPrice;
  private final LocalDate startedAt;

  public BaseSalesHistoryResponse(String status, Long auctionId, String title, String auctionImageUrl,
      int startPrice, LocalDate startedAt) {
    this.status = status;
    this.auctionId = auctionId;
    this.title = title;
    this.auctionImageUrl = auctionImageUrl;
    this.startPrice = startPrice;
    this.startedAt = startedAt;
  }
}
