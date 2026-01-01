package com.windfall.domain.recentview.repository;

import com.windfall.domain.recentview.entity.RecentView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecentViewRepository extends JpaRepository<RecentView, Long> {

  RecentView findByAuctionIdAndUserId(Long auctionId, Long userId);

  boolean existsByAuctionIdAndUserId(Long auctionId, Long userId);

  int countByUserId(Long userId);

  RecentView findTop1ByUserIdOrderByViewedAt(Long userId);
}
