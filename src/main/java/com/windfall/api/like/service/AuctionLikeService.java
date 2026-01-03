package com.windfall.api.like.service;

import com.windfall.api.like.dto.response.AuctionLikeResponse;
import com.windfall.api.like.dto.response.AuctionLikeSupport;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.like.entity.AuctionLike;
import com.windfall.domain.like.repository.AuctionLikeRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    Optional<AuctionLike> existingLike = getLike(auctionId, userId);

    boolean isLiked;

    if (existingLike.isPresent()) {
      AuctionLike like = existingLike.get();

      if (like.isActivated()) {
        auctionLikeRepository.deactivate(like.getId());
        isLiked = false;
      } else {
        auctionLikeRepository.activate(like.getId());
        isLiked = true;
      }
    } else {
      auctionLikeRepository.save(AuctionLike.create(auction, userId));
      isLiked = true;
    }

    long likeCount = getLikeCount(auctionId);

    return AuctionLikeResponse.of(isLiked, likeCount);
  }

  private Auction getAuction(Long auctionId) {
    return auctionRepository.findById(auctionId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_AUCTION));
  }

  private Optional<AuctionLike> getLike(Long auctionId, Long userId) {
    return auctionLikeRepository.findByAuctionIdAndUserId(auctionId, userId);
  }

  @Transactional(readOnly = true)
  public Optional<AuctionLike> getActiveLike(Long auctionId, Long userId) {
    return auctionLikeRepository.findActiveLike(auctionId, userId);
  }

  @Transactional(readOnly = true)
  public long getLikeCount(Long auctionId) {
    return auctionLikeRepository.countByAuctionIdAndActivatedTrue(auctionId);
  }

  @Transactional
  public void deleteLike(Auction auction) {
    auctionLikeRepository.findAllByAuction(auction)
        .forEach(like -> auctionLikeRepository.deactivate(like.getId()));
  }

  @Transactional(readOnly = true)
  public Set<Long> getLikedAuctionIds(
      Long userId, List<? extends AuctionLikeSupport<?>> lists
  ) {
    List<Long> auctionIds = lists.stream()
        .map(AuctionLikeSupport::auctionId)
        .distinct()
        .toList();

    return new HashSet<>(auctionLikeRepository.findLikedAuctionIdsByActivatedTrue(userId, auctionIds));
  }

  public <T extends AuctionLikeSupport<T>> List<T> applyLikeStatus(
      List<T> auctions, Set<Long> likedSet
  ) {
    return auctions.stream()
        .map(info -> info.withIsLiked(likedSet.contains(info.auctionId())))
        .toList();
  }
}