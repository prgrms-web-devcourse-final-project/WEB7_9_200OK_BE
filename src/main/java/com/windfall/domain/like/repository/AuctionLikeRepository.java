package com.windfall.domain.like.repository;

import com.windfall.domain.like.entity.AuctionLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionLikeRepository extends JpaRepository<AuctionLike, Long> {

}