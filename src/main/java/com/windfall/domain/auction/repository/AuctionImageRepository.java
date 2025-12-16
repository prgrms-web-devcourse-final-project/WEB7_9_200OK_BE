package com.windfall.domain.auction.repository;

import com.windfall.domain.auction.entity.AuctionImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionImageRepository extends JpaRepository<AuctionImage, Long>{
  List<AuctionImage> findTop1ByAuctionId(Long auctionId);
}
