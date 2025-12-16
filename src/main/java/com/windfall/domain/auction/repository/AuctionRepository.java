package com.windfall.domain.auction.repository;

import com.windfall.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction,Long>, AuctionRepositoryCustom {

}
