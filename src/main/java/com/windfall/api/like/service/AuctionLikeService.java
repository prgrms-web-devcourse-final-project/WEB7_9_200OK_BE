package com.windfall.api.like.service;

import com.windfall.api.like.dto.response.AuctionLikeResponse;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.like.entity.AuctionLike;
import com.windfall.domain.like.repository.AuctionLikeRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionLikeService {

  private final AuctionLikeRepository auctionLikeRepository;
  private final AuctionRepository auctionRepository;

  @Transactional
  public AuctionLikeResponse toggleLike(Long auctionId, Long userId) {
    Auction auction = getAuction(auctionId);

    Optional<AuctionLike> existingLike = getAuctionLike(userId, auction);

    boolean isLiked;

    if (existingLike.isPresent()) {
      auctionLikeRepository.delete(existingLike.get());
      isLiked = false;
    } else {
      auctionLikeRepository.save(AuctionLike.create(auction, userId));
      isLiked = true;
    }

    long likeCount = auctionLikeRepository.countByAuction(auction);

    return AuctionLikeResponse.of(isLiked, likeCount);
  }

  private Auction getAuction(Long auctionId) {
    return auctionRepository.findById(auctionId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_AUCTION));
  }

  private Optional<AuctionLike> getAuctionLike(Long userId, Auction auction) {
    return auctionLikeRepository.findByAuctionAndUserId(auction, userId);
  }
}