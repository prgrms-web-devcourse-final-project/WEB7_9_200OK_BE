package com.windfall.api.chat.dto.response.info;

import com.windfall.domain.auction.entity.Auction;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경매 상품 정보 DTO")
public record AuctionInfo(

    @Schema(description = "경매 ID")
    Long auctionId,
    @Schema(description = "경매 상품명")
    String title,
    @Schema(description = "경매 대표 이미지 URL")
    String imageUrl
) {
    public static AuctionInfo from(Auction auction, String imageUrl) {
        return new AuctionInfo(
            auction.getId(),
            auction.getTitle(),
            imageUrl
        );
    }
}
