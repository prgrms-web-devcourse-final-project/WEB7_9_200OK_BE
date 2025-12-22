package com.windfall.domain.auction.repository;

import com.windfall.domain.auction.entity.AuctionPriceHistory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionPriceHistoryRepository extends JpaRepository<AuctionPriceHistory, Long> {

  List<AuctionPriceHistory> findTop5ByAuction_IdOrderByCreateDateDesc(Long auctionId);

  Slice<AuctionPriceHistory> findByAuction_IdOrderByCreateDateDesc(Long auctionId, Pageable pageable);

}
