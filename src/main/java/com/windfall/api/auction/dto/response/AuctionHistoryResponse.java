package com.windfall.api.auction.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "경매 가격 변동 내역 응답 DTO")
public record AuctionHistoryResponse(

    @Schema(description = "가격 변동 내역 ID", example = "1")
    Long historyId,

    @Schema(description = "변경된 가격", example = "14850")
    int currentPrice,

    @Schema(description = "하락 퍼센트", example = "1.0")
    double discountRate,

    @Schema(description = "시점 접속자 수", example = "150")
    int viewerCount,

    @Schema(description = "가격 변동 시점", example = "2024-06-01T12:05:00")
    LocalDateTime createdAt

) {}