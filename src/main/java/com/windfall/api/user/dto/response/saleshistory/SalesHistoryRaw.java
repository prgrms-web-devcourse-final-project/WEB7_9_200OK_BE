package com.windfall.api.user.dto.response.saleshistory;

import com.windfall.domain.auction.enums.AuctionStatus;

public record SalesHistoryRaw(
    Long id,
    AuctionStatus status
) {

}
