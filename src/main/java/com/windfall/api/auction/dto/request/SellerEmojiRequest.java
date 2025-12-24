package com.windfall.api.auction.dto.request;

import com.windfall.domain.auction.enums.EmojiType;

public record SellerEmojiRequest(
    EmojiType emojiType
) {

}
