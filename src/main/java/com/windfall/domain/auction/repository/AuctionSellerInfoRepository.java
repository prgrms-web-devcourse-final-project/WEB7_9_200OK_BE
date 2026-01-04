package com.windfall.domain.auction.repository;

import com.windfall.api.auction.dto.response.info.BuyerReviewInfo;
import com.windfall.api.auction.dto.response.raw.SellerAuctionsRaw;
import com.windfall.api.auction.dto.response.stats.SellerReviewStats;
import com.windfall.domain.auction.entity.Auction;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionSellerInfoRepository extends JpaRepository<Auction, Long> {
  @Query("""
  SELECT new com.windfall.api.auction.dto.response.stats.SellerReviewStats(ROUND(CAST(COALESCE(AVG(r.rating / 10.0), 0.0) as double), 1), CAST(COUNT(r.id) as int))
  FROM Review r
  JOIN r.trade t
  WHERE t.sellerId = :sellerId AND t.status = "PURCHASE_CONFIRMED"
""")
  SellerReviewStats getSellerReviewStats(@Param("sellerId") Long sellerId);


  @Query("""
  SELECT new com.windfall.api.auction.dto.response.info.BuyerReviewInfo(u.id, u.nickname, r.content)
  FROM Review r
  JOIN r.trade t
  JOIN User u ON t.buyerId = u.id
  WHERE t.sellerId = :sellerId AND t.status = "PURCHASE_CONFIRMED"
  ORDER BY r.createDate DESC
""")
  List<BuyerReviewInfo> getBuyerInfo(@Param("sellerId") Long sellerId, Pageable pageable);

  @Query("""
  SELECT new com.windfall.api.auction.dto.response.raw.SellerAuctionsRaw(a.id, a.title)
  FROM Auction a
  JOIN a.seller u
  WHERE u.id = :sellerId
  ORDER BY a.createDate DESC
""")
  List<SellerAuctionsRaw> getRawSellerAuctions(@Param("sellerId") Long sellerId, Pageable pageable);
}
