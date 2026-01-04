package com.windfall.api.auction.dto.response.info;

import com.windfall.api.auction.dto.response.raw.SellerAuctionsRaw;

public record SellerAuctionsInfo(

    Long auctionId,
    String auctionImageUrl,
    String title

) {

  public static SellerAuctionsInfo of(SellerAuctionsRaw raw, String image){
    return new SellerAuctionsInfo(raw.auctionId(), image, raw.title());
  }
}
