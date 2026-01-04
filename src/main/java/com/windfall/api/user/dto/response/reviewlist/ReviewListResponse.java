package com.windfall.api.user.dto.response.reviewlist;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewListResponse{

  private final Long reviewId;
  private final Long auctionId;
  private final Long buyerId;
  private final String nickname;
  private final String userImageUrl;
  private final int rating;
  private final String content;
  private final String auctionImageUrl;
  private final String auctionTitle;

  @Builder
  public ReviewListResponse(Long reviewId, Long auctionId, Long buyerId, String nickname,
      String userImageUrl, int rating, String content, String auctionImageUrl,
      String auctionTitle) {
    this.reviewId = reviewId;
    this.auctionId = auctionId;
    this.buyerId = buyerId;
    this.nickname = nickname;
    this.userImageUrl = userImageUrl;
    this.rating = rating;
    this.content = content;
    this.auctionImageUrl = auctionImageUrl;
    this.auctionTitle = auctionTitle;
  }

  public static ReviewListResponse of(ReviewListRaw raw, String auctionImageUrl){
    return ReviewListResponse.builder()
        .reviewId(raw.reviewId())
        .auctionId(raw.auctionId())
        .buyerId(raw.buyerId())
        .nickname(raw.nickname())
        .userImageUrl(raw.userProfileImage())
        .rating(raw.rating())
        .content(raw.content())
        .auctionImageUrl(auctionImageUrl)
        .auctionTitle(raw.auctionTitle())
        .build();
  }

}
