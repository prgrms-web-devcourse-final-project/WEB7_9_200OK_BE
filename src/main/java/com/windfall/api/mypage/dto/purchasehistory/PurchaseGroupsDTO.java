package com.windfall.api.mypage.dto.purchasehistory;

import com.windfall.domain.trade.enums.TradeStatus;
import java.util.List;
import java.util.Map;

public record PurchaseGroupsDTO(
    Map<TradeStatus, List<Long>> tradeGroups,
    Map<TradeStatus, List<Long>> auctionGroups
) {
}
