package com.windfall.api.mypage.dto.dashboard;

import com.windfall.domain.auction.enums.AuctionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record DashBoardDetailsRaw(
    @Schema(description = "경매 id")
    Long id,

    @Schema(description = "경매 상태")
    AuctionStatus status
) {

}
