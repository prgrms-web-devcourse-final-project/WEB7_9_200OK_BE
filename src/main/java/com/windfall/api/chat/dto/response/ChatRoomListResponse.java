package com.windfall.api.chat.dto.response;

import com.windfall.api.chat.dto.response.info.AuctionInfo;
import com.windfall.api.chat.dto.response.info.LastMessageInfo;
import com.windfall.api.chat.dto.response.info.PartnerInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 목록 아이템 DTO")
public record ChatRoomListResponse(

    @Schema(description = "채팅방 ID")
    Long chatRoomId,

    @Schema(description = "거래 ID")
    Long tradeId,

    @Schema(description = "상대방 정보")
    PartnerInfo partner,

    @Schema(description = "경매 상품 정보")
    AuctionInfo auction,

    @Schema(description = "마지막 메시지 정보")
    LastMessageInfo lastMessage,

    @Schema(description = "읽지 않은 메시지 수")
    long unreadCount
) {}
