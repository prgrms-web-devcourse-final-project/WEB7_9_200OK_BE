package com.windfall.api.mypage.dto.auctionlikelist;

import com.windfall.domain.auction.enums.AuctionStatus;

public record AuctionLikeListRaw(
    Long id,
    AuctionStatus status
) {

}
