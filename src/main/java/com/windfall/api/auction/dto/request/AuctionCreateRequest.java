package com.windfall.api.auction.dto.request;

import com.windfall.domain.auction.enums.AuctionCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import java.util.List;


@Schema(description = "경매 생성 요청 DTO")
public record AuctionCreateRequest(

    @NotNull(message = "판매자 id는 필수입니다.")
    @Schema(description = "판매자 id", example = "1")
    Long sellerId,

    @NotBlank(message = "경매 제목은 필수입니다.")
    @Schema(description = "경매 제목", example = "테스트 제목")
    String title,

    @NotBlank(message = "경매 설명은 필수입니다.")
    @Schema(description = "경매 설명", example = "테스트 설명")
    String description,

    @NotNull(message = "경매 카테고리는 필수입니다.")
    @Schema(description = "경매 카테고리", example = "1")
    AuctionCategory category,

    @Schema(description = "경매 태그", example = "나이키")
    List<String> tag,

    @NotNull(message = "경매 시작가는 필수입니다.")
    @Schema(description = "경매 시작가", example = "10000")
    Long startPrice,

    @NotNull(message = "경매 스탑 로스는 필수입니다.")
    @Schema(description = "경매 스탑 로스", example = "9000")
    Long stopLoss,

    @NotNull(message = "경매 하락 금액은 필수입니다.")
    @Schema(description = "경매 하락 금액", example = "50")
    Long dropAmount,

    @NotNull(message = "경매 시작 시간은 필수입니다.")
    @Schema(description = "경매 시작 시간", example = "2025-12-09T16:50:51")
    LocalDateTime startAt
) {
}