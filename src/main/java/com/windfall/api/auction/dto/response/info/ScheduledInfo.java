package com.windfall.api.auction.dto.response.info;

import com.windfall.api.like.dto.response.AuctionLikeSupport;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Bool;

@Schema(description = "경매 예정 응답 DTO")
public record ScheduledInfo(
    @Schema(description = "경매 ID")
    Long auctionId,

    @Schema(description = "상품 이미지 URL 목록")
    String imageUrl,

    @Schema(description = "상품 제목")
    String title,

    @Schema(description = "시작가")
    Long startPrice,

    @Schema(description = "사용자 찜 여부")
    Boolean isLiked,

    @Schema(description = "경매 시작 시간")
    LocalDateTime startedAt,

    @Schema(description = "경매 시작 알림 여부")
    Boolean isNotification
) implements AuctionLikeSupport<ScheduledInfo> {

  @Override
  public ScheduledInfo withIsLiked(boolean isLiked) {
    return new ScheduledInfo(
        auctionId,
        imageUrl,
        title,
        startPrice,
        isLiked,
        startedAt,
        isNotification
    );
  }
}
