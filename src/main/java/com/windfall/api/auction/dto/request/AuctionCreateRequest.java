package com.windfall.api.auction.dto.request;

import com.windfall.domain.auction.enums.AuctionCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.NonNull;


@Schema(description = "경매 생성 응답 DTO")
public record AuctionCreateRequest(
    @NonNull
    @Schema(description = "판매자 id", example = "1")
    Long sellerId,

    @NonNull
    @Schema(description = "경매 제목", example = "테스트 제목")
    String title,

    @NonNull
    @Schema(description = "경매 설명", example = "테스트 설명")
    String description,

    @NonNull
    @Schema(description = "경매 카테고리", example = "1")
    AuctionCategory category,

    @NonNull
    @Schema(description = "경매 시작가", example = "10000")
    Long startPrice,

    @NonNull
    @Schema(description = "경매 스탑 로스", example = "9000")
    Long stopLoss,

    @NonNull
    @Schema(description = "경매 하락 금액", example = "50")
    Long dropAmount,

    @NonNull
    @Schema(description = "경매 시작 시간", example = "2025-12-09 16:50:51:23")
    LocalDateTime startAt
) {
}
