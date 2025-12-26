package com.windfall.domain.tag.repository;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionTagRepository extends JpaRepository<AuctionTag, Long> {

  List<AuctionTag> findAllByAuction(Auction auction);

  boolean existsByTag(Tag tag);

  @Query("""
    select at
    from AuctionTag at
    join fetch at.tag t
    where t.tagName like concat('%', :keyword, '%')
""")
  List<AuctionTag> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}