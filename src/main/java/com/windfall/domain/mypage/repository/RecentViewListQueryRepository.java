package com.windfall.domain.mypage.repository;

import com.windfall.api.mypage.dto.recentviewlist.RecentViewListRaw;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionStatus;
import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecentViewListQueryRepository extends JpaRepository<Auction, Long> {

  @Query("""
  SELECT new com.windfall.api.mypage.dto.recentviewlist.RecentViewListRaw(a.id, a.status)
  FROM RecentView rv
  JOIN Auction a ON rv.auction.id = a.id
  WHERE
  rv.userId = :id AND
  a.status = COALESCE(:filter, a.status)
  ORDER BY rv.viewedAt DESC
""")
  Slice<RecentViewListRaw> getRawRecentViewList(@Param("id") Long userId, @Param("filter") AuctionStatus filter, Pageable pageable);

  @Query(value = """
  SELECT
  rv.id AS recentViewId,
  a.status AS status, -- 경매 상태 (일반, 취소)
  a.id AS auctionId, -- 경매 아이디
  a.title AS title, -- 경매 이름 (상품 이름)
  ai.image AS auctionImageUrl, -- 경매 이미지 url
  a.start_price AS startPrice, -- 시작가
  DATE(a.started_at) AS startedAt -- 경매 시작일
  FROM auction a
  JOIN recent_view rv ON rv.auction_id = a.id
  LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i 
      WHERE i.auction_id IN (:auctionIds)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
  WHERE a.id IN(:auctionIds) AND rv.user_id = :userId;
""", nativeQuery = true)
  List<Tuple> getRecentViewList(@Param("auctionIds") List<Long> auctionIds, @Param("userId") Long userId);

  @Query(value = """
  SELECT
  rv.id AS recentViewId,
  a.status AS status, -- 경매 상태 (완료)
  a.id AS auctionId, -- 경매 아이디
  a.title AS title, -- 경매 이름 (상품 이름)
  ai.image AS auctionImageUrl, -- 경매 이미지 url
  a.start_price AS startPrice, -- 시작가
  t.final_price AS endPrice, -- 낙찰가
  ROUND(((a.start_price - t.final_price) / a.start_price) * 100, 0) AS discountPercent, -- 할인율
  DATE(a.started_at) AS startedAt, -- 경매 시작일
  t.status AS tradeStatus -- 거래 상태
  FROM auction a
  JOIN recent_view rv ON rv.auction_id = a.id
  JOIN trade t ON t.auction_id = a.id
  LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i 
      WHERE i.auction_id IN (:auctionIds)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
  WHERE a.id IN(:auctionIds) AND rv.user_id = :userId;
  """, nativeQuery = true)
  List<Tuple> getCompletedRecentViewList(@Param("auctionIds") List<Long> auctionIds, @Param("userId") Long userId);

  @Query(value = """
  SELECT
  rv.id AS recentViewId,
  a.status AS status, -- 경매 상태 (진행 중, 유찰)
  a.id AS auctionId, -- 경매 아이디
  a.title AS title, -- 경매 이름 (상품 이름)
  ai.image AS auctionImageUrl, -- 경매 이미지 url
  a.start_price AS startPrice, -- 시작가
  a.current_price AS currentPrice, -- 현재가
  ROUND(((a.start_price - a.current_price) / a.start_price) * 100, 0) AS discountPercent, -- 할인율
  DATE(a.started_at) AS startedAt -- 경매 시작일
  FROM auction a
  JOIN recent_view rv ON rv.auction_id = a.id
  LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i 
      WHERE i.auction_id IN (:auctionIds)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
  WHERE a.id IN(:auctionIds) AND rv.user_id = :userId;
  """, nativeQuery = true)
  List<Tuple> getProcessingRecentViewList(@Param("auctionIds") List<Long> auctionIds, @Param("userId") Long userId);

}
