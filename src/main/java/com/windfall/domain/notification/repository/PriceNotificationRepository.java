package com.windfall.domain.notification.repository;

import com.windfall.domain.notification.entity.PriceNotification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PriceNotificationRepository extends JpaRepository<PriceNotification,Long> {
  @Query("""
select a from PriceNotification a
where a.auctionId = :auctionId
  and a.targetPrice >= :currentPrice
  and a.notified = false
""")
  List<PriceNotification> findNotNotified(
      @Param("auctionId") Long auctionId,
      @Param("currentPrice") Long currentPrice
  );

  Optional<PriceNotification> findByUserIdAndAuctionId(Long userId, Long auctionId);
}
