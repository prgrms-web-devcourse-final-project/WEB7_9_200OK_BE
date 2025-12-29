package com.windfall.api.mypage.dto.notificationsetlist;

import com.windfall.domain.auction.enums.AuctionStatus;

public record NotificationSetListRaw(
    Long id,
    AuctionStatus status
) {

}
