package com.windfall.domain.like.repository;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.like.entity.AuctionLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionLikeRepository extends JpaRepository<AuctionLike, Long> {

  Optional<AuctionLike> findByAuctionIdAndUserId(Long auctionId, Long userId);

  @Query("""
        select al
        from AuctionLike al
        where al.auction.id = :auctionId
          and al.userId = :userId
          and al.activated = true
      """)
  Optional<AuctionLike> findActiveLike(
      @Param("auctionId") Long auctionId,
      @Param("userId") Long userId
  );

  long countByAuctionIdAndActivatedTrue(Long auctionId);

  @Modifying
  @Query("""
          update AuctionLike al
          set al.activated = false
          where al.id = :id
      """)
  void deactivate(@Param("id") Long id);

  @Modifying
  @Query("""
          update AuctionLike al
          set al.activated = true
          where al.id = :id
      """)
  void activate(@Param("id") Long id);

  List<AuctionLike> findAllByAuction(Auction auction);
}