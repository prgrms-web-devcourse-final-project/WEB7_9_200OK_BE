package com.windfall.domain.mypage.repository;

import com.windfall.api.mypage.dto.notificationsetlist.NotificationSetListRaw;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionStatus;
import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationSetListQueryRepository extends JpaRepository<Auction, Long> {


  @Query("""
    SELECT new com.windfall.api.mypage.dto.notificationsetlist.NotificationSetListRaw(a.id, a.status)
    FROM NotificationSetting ns
    JOIN Auction a ON ns.auction.id = a.id
    WHERE
    ns.user.id = :id AND
    a.status = COALESCE(:filter, a.status) AND
    ns.activated = true
    GROUP BY a.id, a.status
    ORDER BY a.createDate DESC
  """)
  Slice<NotificationSetListRaw> getRawNotification(@Param("id") Long userId, @Param("filter") AuctionStatus filter, Pageable pageable);

  @Query(value = """
    SELECT
    a.status AS status, -- 경매 상태 (일반, 취소)
    a.id AS auctionId, -- 경매 아이디
    a.title AS title, -- 상품 이미지
    ai.image AS auctionImageUrl, -- 경매 이미지 url
    a.start_price AS startPrice, -- 시작가
    DATE(a.started_at) AS startedAt, -- 시작 일시
    MAX(ns.type = "AUCTION_START_WISHLIST" AND ns.activated = true) AS alertStart, -- 경매 시작 알림 T/F
    MAX(ns.type = "AUCTION_ENDED_OTHER" AND ns.activated = true) AS alertEnd, -- 경매 종료 알림 T/F
    MAX(ns.type = "PRICE_DROP" AND ns.activated = true) AS alertPrice, -- 경매 가격 도달 알림 T/F
    MAX(COALESCE(pn.price_alert, 0)) AS triggerPrice -- 경매 가격 감지 알림
    FROM auction a
    JOIN notification_setting ns ON ns.auction_id = a.id
    LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i 
      WHERE i.auction_id IN (:auctionIds)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
    LEFT JOIN price_notification pn ON pn.setting_id = ns.id
    WHERE a.id IN(:auctionIds)
    GROUP BY a.status, a.id, a.title, ai.image, a.start_price, a.started_at;
    """, nativeQuery = true)
  List<Tuple> getNotificationSetList(@Param("auctionIds") List<Long> auctionIds);

  @Query(value = """
    SELECT
    a.status AS status, -- 경매 상태 (성공)
    a.id AS auctionId, -- 경매 아이디
    a.title AS title, -- 상품 이미지
    ai.image AS auctionImageUrl, -- 경매 이미지 url
    a.start_price AS startPrice, -- 시작가
    t.final_price AS endPrice, -- 낙찰가
    ROUND(((a.start_price - t.final_price) / a.start_price) * 100) AS discountPercent, -- 할인율
    DATE(a.started_at) AS startedAt, -- 시작 일시
    MAX(ns.type = "AUCTION_START_WISHLIST" AND ns.activated = true) AS alertStart, -- 경매 시작 알림 T/F
    MAX(ns.type = "AUCTION_ENDED_OTHER" AND ns.activated = true) AS alertEnd, -- 경매 종료 알림 T/F
    MAX(ns.type = "PRICE_DROP" AND ns.activated = true) AS alertPrice, -- 경매 가격 도달 알림 T/F
    MAX(COALESCE(pn.price_alert, 0)) AS triggerPrice, -- 경매 가격 감지 알림
    t.status AS tradeStatus
    FROM auction a
    JOIN trade t ON t.auction_id = a.id
    JOIN notification_setting ns ON ns.auction_id = a.id
    LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i 
      WHERE i.auction_id IN (:auctionIds)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
    LEFT JOIN price_notification pn ON pn.setting_id = ns.id
    WHERE a.id IN(:auctionIds)
    GROUP BY a.status, a.id, a.title, ai.image, a.start_price, a.started_at, t.status, t.final_price;
    """, nativeQuery = true)
  List<Tuple> getCompletedNotificationSetList(@Param("auctionIds") List<Long> auctionIds);

  @Query(value = """
    SELECT
    a.status AS status, -- 경매 상태 (진행, 유찰)
    a.id AS auctionId, -- 경매 아이디
    a.title AS title, -- 상품 이미지
    ai.image AS auctionImageUrl, -- 경매 이미지 url
    a.start_price AS startPrice, -- 시작가
    a.current_price AS currentPrice, -- 현재가
    ROUND(((a.start_price - a.current_price) / a.start_price) * 100) AS discountPercent, -- 할인율
    DATE(a.started_at) AS startedAt, -- 시작 일시
    MAX(ns.type = "AUCTION_START_WISHLIST" AND ns.activated = true) AS alertStart, -- 경매 시작 알림 T/F
    MAX(ns.type = "AUCTION_ENDED_OTHER" AND ns.activated = true) AS alertEnd, -- 경매 종료 알림 T/F
    MAX(ns.type = "PRICE_DROP" AND ns.activated = true) AS alertPrice, -- 경매 가격 도달 알림 T/F
    MAX(COALESCE(pn.price_alert, 0)) AS triggerPrice -- 경매 가격 감지 알림
    FROM auction a
    JOIN notification_setting ns ON ns.auction_id = a.id
    LEFT JOIN (
    SELECT i.auction_id as auction_id, MIN(i.id) as first_image_id
      FROM auction_image i 
      WHERE i.auction_id IN (:auctionIds)
      GROUP BY i.auction_id
    ) x ON x.auction_id = a.id
    LEFT JOIN auction_image ai ON x.first_image_id = ai.id  -- 각 경매별 가장 첫 번째 이미지 뽑기
    LEFT JOIN price_notification pn ON pn.setting_id = ns.id
    WHERE a.id IN (:auctionIds)
    GROUP BY a.status, a.id, a.title, ai.image, a.start_price, a.current_price, a.started_at;
    """, nativeQuery = true)
  List<Tuple> getProcessingNotificationSetList(@Param("auctionIds") List<Long> auctionIds);


}
