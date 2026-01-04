package com.windfall.api.auction.dto.response;

import com.windfall.api.auction.dto.response.info.BuyerReviewInfo;
import com.windfall.api.auction.dto.response.info.SellerAuctionsInfo;
import com.windfall.api.auction.dto.response.stats.SellerReviewStats;
import com.windfall.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AuctionSellerInfoResponse(
    @Schema(description = "판매자 ID")
    Long sellerId,

    @Schema(description = "판매자 이름")
    String username,

    @Schema(description = "평균 별점")
    double rating,

    @Schema(description = "총 받은 리뷰 수")
    int totalReviews,

    @Schema(description = "구매자 리뷰 (최신순 최대 4건)")
    List<BuyerReviewInfo> buyers,

    @Schema(description = "판매자가 올린 경매 상품 (최신순 최대 10건)")
    List<SellerAuctionsInfo> auctions
) {

  public static AuctionSellerInfoResponse of(User user, SellerReviewStats stats, List<BuyerReviewInfo> info, List<SellerAuctionsInfo> auctions){
    return new AuctionSellerInfoResponse(
        user.getId(),
        user.getNickname(),
        stats.rating(),
        stats.totalReviews(),
        info,
        auctions);
  }

}
