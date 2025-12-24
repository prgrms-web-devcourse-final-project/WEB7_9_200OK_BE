package com.windfall.api.auction.dto.response.message;

import com.windfall.domain.auction.enums.EmojiType;

public record SellerEmojiMessage(
    Long auctionId,
    EmojiType emojiType
) {

  public static SellerEmojiMessage of(Long auctionId, EmojiType emojiType) {
    return new SellerEmojiMessage(
        auctionId,
        emojiType
    );
  }
}
