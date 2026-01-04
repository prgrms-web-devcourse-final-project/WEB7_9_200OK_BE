package com.windfall.api.user.dto.response.reviewlist;

import java.time.LocalDateTime;

public record ReviewListRaw(
    Long reviewId,
    Long auctionId,
    Long buyerId,
    String nickname,
    String userProfileImage,
    LocalDateTime reviewedAt,
    int rating,
    String content,
    String auctionTitle
) {

}
