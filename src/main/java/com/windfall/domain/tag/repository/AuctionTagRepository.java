package com.windfall.domain.tag.repository;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionTagRepository extends JpaRepository<AuctionTag, Long> {

  List<AuctionTag> findByAuction(Auction auction);

  boolean existsByTag(Tag tag);
}