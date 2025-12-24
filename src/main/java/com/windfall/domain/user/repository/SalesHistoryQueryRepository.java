package com.windfall.domain.user.repository;

import com.windfall.api.user.dto.response.saleshistory.SalesHistoryRaw;
import com.windfall.domain.auction.entity.Auction;
import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesHistoryQueryRepository extends JpaRepository<Auction, Long> {

  @Query("""
    SELECT
    new com.windfall.api.user.dto.response.saleshistory.SalesHistoryRaw(
    a.id,
    a.status)
    FROM Auction a
    WHERE a.seller.id = :id AND
    a.status = COALESCE(:filter, a.status) AND
    a.activated = true
    ORDER BY a.startedAt DESC
  """)
  Slice<SalesHistoryRaw> getRawSalesHistory(@Param("id") Long userId, @Param("filter") String filter, Pageable pageable);

  @Query(value = """
    SELECT
    a.status AS status, -- 경매 상태 (예정, 취소 (기본))
    a.id AS auctionId, -- 경매 id
    a.title AS title, -- 경매 상품 이름
    ai.image AS auctionImageUrl, -- 경매 상품 이미지 (첫 번째)
    a.start_price AS startPrice, -- 경매 상품 시작가
    DATE(a.started_at) AS startedAt -- 경매 시작 시간
    FROM auction a
    LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i 
      WHERE i.auction_id IN (:ids)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
    WHERE a.id IN(:ids)
  """, nativeQuery = true)
  List<Tuple> getSalesHistory(@Param("ids") List<Long> ids);

  @Query(value = """
    SELECT
    a.status AS status, -- 경매 상태 (완료, 사용자가 아닐 경우)
    a.id AS auctionId, -- 경매 id
    a.title AS title, -- 경매 상품 이름
    ai.image AS auctionImageUrl, -- 경매 상품 사진
    a.start_price AS startPrice, -- 경매 시작가
    t.final_price AS endPrice, -- 낙찰가
    ROUND(((a.start_price - t.final_price) / a.start_price) * 100) AS discountPercent, -- 할인율
    DATE(a.started_at) AS startedAt, -- 경매 시작일
    t.status AS tradeStatus -- 거래 상태
    FROM auction a
    JOIN trade t ON a.id = t.auction_id
    LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i
      WHERE i.auction_id IN (:ids)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
    WHERE a.id IN(:ids) AND (t.status = "PAYMENT_COMPLETED" OR t.status = "PURCHASE_CONFIRMED")
  """, nativeQuery = true)
  List<Tuple> getCompletedSalesHistory(@Param("ids") List<Long> ids);

  @Query(value = """
   SELECT
   a.status AS status, -- 경매 상태 (완료, 사용자일 경우)
   a.id AS auctionId, -- 경매 id
   a.title AS title, -- 경매 상품 이름
   ai.image AS auctionImageUrl, -- 경매 상품 사진
   a.start_price AS startPrice, -- 경매 시작가
   t.final_price AS endPrice, -- 낙찰가
   ROUND(((a.start_price - t.final_price) / a.start_price) * 100) AS discountPercent, -- 할인율
   DATE(a.started_at) AS startedAt, -- 경매 시작일
   t.status AS tradeStatus, -- 거래 상태
   cr.id AS roomId, -- 채팅방 id
   COALESCE(SUM(cm.sender_id != :id AND cm.is_read = false), 0) AS unreadCount-- 안 읽은 채팅 개수
   FROM auction a
   JOIN trade t ON a.id = t.auction_id
   JOIN chat_room cr ON t.id = cr.trade_id
   LEFT JOIN chat_message cm ON cr.id = cm.chat_room_id
   LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i
      WHERE i.auction_id IN (:ids)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
   WHERE a.id IN(:ids) AND (t.status = "PAYMENT_COMPLETED" OR t.status = "PURCHASE_CONFIRMED")
   GROUP BY a.status, a.id, a.title, ai.image, a.start_price, a.started_at, t.final_price, t.status, cr.id
  """, nativeQuery = true)
  List<Tuple> getOwnerCompletedSalesHistory(@Param("ids") List<Long> ids, @Param("id") Long userid);

  @Query(value = """
   SELECT
   a.status AS status, -- 경매 상태 (진행중이거나 유찰중일 경우)
   a.id AS auctionId, -- 경매 id
   a.title AS title, -- 경매 상품 이름
   ai.image AS auctionImageUrl, -- 경매 상품 사진
   a.start_price AS startPrice, -- 경매 시작가
   a.current_price AS currentPrice, -- 현재가
   ROUND(((a.start_price - a.current_price) / a.start_price) * 100, 0) AS discountPercent, -- 할인율
   DATE(a.started_at) AS startedAt -- 경매 시작일
   FROM auction a
   LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i
      WHERE i.auction_id IN (:ids)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
   WHERE a.id IN(:ids)
  """, nativeQuery = true)
  List<Tuple> getProcessingSalesHistory(@Param("ids") List<Long> ids);
}
