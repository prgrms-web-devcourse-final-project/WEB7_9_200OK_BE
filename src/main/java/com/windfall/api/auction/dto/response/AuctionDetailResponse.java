package com.windfall.api.auction.dto.response;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.api.auction.dto.response.info.SellerInfo;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "경매 상세 응답 DTO")
public record AuctionDetailResponse(

    @Schema(description = "경매 ID")
    Long auctionId,

    @Schema(description = "상품 제목")
    String title,

    @Schema(description = "상품 설명")
    String description,

    @Schema(description = "카테고리")
    AuctionCategory category,

    @Schema(description = "상품 이미지 URL 목록")
    List<String> imageUrls,

    @Schema(description = "판매자 정보")
    SellerInfo seller,

    @Schema(description = "시작가")
    Long startPrice,

    @Schema(description = "현재가 (예정 : 시작가)")
    Long currentPrice,

    @Schema(description = "최소 보장가 (판매자만)")
    Long stopLoss,

    @Schema(description = "하락 퍼센트 (예정 : null)")
    Double discountRate,

    @Schema(description = "경매 상태")
    AuctionStatus status,

    @Schema(description = "찜 수")
    Long likeCount,

    @Schema(description = "사용자 찜 여부")
    boolean isLiked,

    @Schema(description = "실시간 접속자 수")
    Long viewerCount,

    @Schema(description = "경매 시작 시간")
    LocalDateTime startedAt,

    @Schema(description = "최근 가격 하락 내역")
    List<AuctionHistoryResponse> recentPriceHistory

) {
  public static AuctionDetailResponse of(
      Auction auction,
      Long currentPrice,
      Double discountRate,
      Long stopLoss,
      boolean isLiked,
      Long viewerCount,
      List<AuctionHistoryResponse> history
  ) {
    return new AuctionDetailResponse(
        auction.getId(),
        auction.getTitle(),
        auction.getDescription(),
        auction.getCategory(),
        List.of("https://example.jpg"), // auction.getImageUrls(),
        SellerInfo.from(auction.getSeller()),
        auction.getStartPrice(),
        currentPrice,
        stopLoss,
        discountRate,
        auction.getStatus(),
        0L, // auction.getLikeCount(),
        isLiked,
        viewerCount,
        auction.getStartedAt(),
        history
    );
  }
}

