package com.windfall.domain.auctionlike.repository;

import com.windfall.domain.auctionlike.entity.AuctionLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionLikeRepository extends JpaRepository<AuctionLike, Long> {

}