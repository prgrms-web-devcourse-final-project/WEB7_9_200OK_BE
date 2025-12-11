package com.windfall.api.auction.dto.response;

import com.windfall.domain.auction.entity.AuctionPriceHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "경매 가격 변동 내역 응답 DTO")
public record AuctionHistoryResponse(

    @Schema(description = "가격 변동 내역 ID")
    Long historyId,

    @Schema(description = "변경된 가격")
    Long currentPrice,

    @Schema(description = "시점 접속자 수")
    Long viewerCount,

    @Schema(description = "가격 변동 시점")
    LocalDateTime createdAt

) {
  public static AuctionHistoryResponse from(AuctionPriceHistory history) {
    return new AuctionHistoryResponse(
        history.getId(),
        history.getPrice(),
        history.getViewerCount(),
        history.getCreateDate()
    );
  }
}