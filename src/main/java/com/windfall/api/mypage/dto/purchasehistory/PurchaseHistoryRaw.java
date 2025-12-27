package com.windfall.api.mypage.dto.purchasehistory;

import com.windfall.domain.trade.enums.TradeStatus;

public record PurchaseHistoryRaw(
    Long auctionId,
    Long tradeId,
    TradeStatus status
) {

}
