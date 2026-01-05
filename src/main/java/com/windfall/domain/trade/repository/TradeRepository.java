package com.windfall.domain.trade.repository;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.trade.entity.Trade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
  Optional<Trade> findByAuction(Auction auction);

  Optional<Trade> findById(Long id);
}
