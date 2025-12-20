package com.windfall.domain.like.repository;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.like.entity.AuctionLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionLikeRepository extends JpaRepository<AuctionLike, Long> {

  Optional<AuctionLike> findByAuctionAndUserId(Auction auction, Long userId);

  long countByAuction(Auction auction);
}