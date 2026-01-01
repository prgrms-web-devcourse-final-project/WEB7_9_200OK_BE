package com.windfall.api.mypage.dto.recentviewlist;

import com.windfall.domain.auction.enums.AuctionStatus;

public record RecentViewListRaw(
    Long id,
    AuctionStatus status
) {

}
