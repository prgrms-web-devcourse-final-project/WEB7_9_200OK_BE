package com.windfall.domain.mypage.repository;

import com.windfall.api.mypage.dto.purchasehistory.PurchaseHistoryRaw;
import com.windfall.api.user.dto.response.saleshistory.SalesHistoryRaw;
import com.windfall.domain.trade.entity.Trade;
import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseHistoryQueryRepository extends JpaRepository<Trade, Long> {

  @Query("""
    SELECT
    new com.windfall.api.mypage.dto.purchasehistory.PurchaseHistoryRaw(
    a.id,
    t.id,
    t.status)
    FROM Trade t
    JOIN Auction a ON t.auction.id = a.id
    WHERE t.buyerId = :id AND
    t.status = COALESCE(:filter, t.status) AND
    (t.status = "PAYMENT_COMPLETED" OR t.status = "PURCHASE_CONFIRMED")
    ORDER BY t.createDate DESC
  """)
  Slice<PurchaseHistoryRaw> getRawPurchaseHistory(@Param("id") Long userId, @Param("filter") String filter, Pageable pageable);

  @Query(value = """
    SELECT
    t.status AS status, -- 거래 상태
    a.id AS auctionId, -- 경매 id
    a.title AS title, -- 상품 이름
    ai.image AS auctionImageUrl, -- 상품 대표 사진
    a.start_price AS startPrice, -- 시작가
    t.final_price AS endPrice, -- 낙찰가
    ROUND(((a.start_price - t.final_price) / a.start_price) * 100) AS discountPercent, -- 할인율
    DATE(t.create_date) AS purchasedDate, -- 낙찰 일시
    cr.id AS roomId, -- 채팅방 id
    COALESCE(SUM(cm.sender_id != :id AND cm.is_read = false), 0) AS unreadCount -- 안 읽은 채팅 개수
    FROM trade t
    JOIN auction a ON t.auction_id = a.id
    JOIN chat_room cr ON cr.trade_id = t.id
    LEFT JOIN chat_message cm ON cm.chat_room_id = cr.id
    LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i 
      WHERE i.auction_id IN (:auctionIds)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
    WHERE t.id IN(:ids)
    GROUP BY t.status, a.id, a.title, ai.image, a.start_price, t.final_price, t.create_date, cr.id
""", nativeQuery = true)
  List<Tuple> getPurchaseHistory(@Param("id") Long userid, @Param("ids") List<Long> ids, @Param("auctionIds") List<Long> auctionIds);

  @Query(value = """
    SELECT
    t.status AS status, -- 거래 상태
    a.id AS auctionId, -- 경매 id
    a.title AS title, -- 상품 이름
    ai.image AS auctionImageUrl, -- 상품 대표 사진
    a.start_price AS startPrice, -- 시작가
    t.final_price AS endPrice, -- 낙찰가
    ROUND(((a.start_price - t.final_price) / a.start_price) * 100) AS discountPercent, -- 할인율
    DATE(t.create_date) AS purchasedDate, -- 낙찰 일시
    cr.id AS roomId, -- 채팅방 id
    COALESCE(SUM(cm.sender_id != :id AND cm.is_read = false), 0) AS unreadCount, -- 안 읽은 채팅 개수
    COALESCE(r.id, 0) AS reviewId -- 리뷰 id
    FROM trade t
    JOIN auction a ON t.auction_id = a.id
    JOIN chat_room cr ON cr.trade_id = t.id
    LEFT JOIN review r ON r.trade_id = t.id
    LEFT JOIN chat_message cm ON cm.chat_room_id = cr.id
    LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i 
      WHERE i.auction_id IN (:auctionIds)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
    WHERE t.id IN(:ids)
    GROUP BY t.status, a.id, a.title, ai.image, a.start_price, t.final_price, t.create_date, cr.id, r.id
""", nativeQuery = true)
  List<Tuple> getConfirmedPurchaseHistory(@Param("id") Long userid, @Param("ids") List<Long> ids, @Param("auctionIds") List<Long> auctionIds);
}
