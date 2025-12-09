package com.windfall.api.auction.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.NonNull;

@Schema(description = "경매 생성 응답 DTO")
public record AuctionCreateResponse(
    @NonNull
    @Schema(description = "판매자 id")
    Long sellerId,

    @NonNull
    @Schema(description = "경매 제목")
    String title,

    @NonNull
    @Schema(description = "경매 설명")
    String description,

    @NonNull
    @Schema(description = "경매 카테고리")
    AuctionCategory category,

    @NonNull
    @Schema(description = "경매 시작가")
    Long startPrice,

    @NonNull
    @Schema(description = "경매 현재가")
    Long currentPrice,

    @NonNull
    @Schema(description = "경매 스탑 로스")
    Long stopLoss,

    @NonNull
    @Schema(description = "경매 하락 금액")
    Long dropAmount,

    @NonNull
    @Schema(description = "경매 상태")
    AuctionStatus status,

    @NonNull
    @Schema(description = "경매 시작 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime startAt
) {

  public static AuctionCreateResponse from(Auction auction, Long sellerId) {
    return new AuctionCreateResponse(
        sellerId,
        auction.getTitle(),
        auction.getDescription(),
        auction.getCategory(),
        auction.getStartPrice(),
        auction.getCurrentPrice(),
        auction.getStopLoss(),
        auction.getDropAmount(),
        auction.getStatus(),
        auction.getStartedAt()
    );
  }
}
