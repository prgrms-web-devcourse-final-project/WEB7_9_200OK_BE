package com.windfall.api.like.dto.response;

public interface AuctionLikeSupport<T extends AuctionLikeSupport<T>> {

  Long auctionId();

  T withIsLiked(boolean isLiked);
}