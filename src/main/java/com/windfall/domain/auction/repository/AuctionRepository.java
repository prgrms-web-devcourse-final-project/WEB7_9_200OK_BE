package com.windfall.domain.auction.repository;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction,Long>, AuctionRepositoryCustom {

  List<Auction> findAllByStatusAndStartedAtLessThanEqual(AuctionStatus status, LocalDateTime now);

  List<Auction> findAllByStatus(AuctionStatus status);
}
