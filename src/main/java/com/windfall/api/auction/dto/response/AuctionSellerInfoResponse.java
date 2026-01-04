package com.windfall.api.auction.dto.response;

import com.windfall.api.auction.dto.response.info.BuyerReviewInfo;
import com.windfall.api.auction.dto.response.info.SellerAuctionsInfo;
import com.windfall.api.auction.dto.response.stats.SellerReviewStats;
import com.windfall.domain.user.entity.User;
import java.util.List;

public record AuctionSellerInfoResponse(
    Long sellerId,
    String username,
    double rating,
    int totalReviews,
    List<BuyerReviewInfo> buyers,
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
