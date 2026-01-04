package com.windfall.domain.auction.repository;

import com.windfall.api.user.dto.response.reviewlist.AuctionImageRaw;
import com.windfall.domain.auction.entity.AuctionImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionImageRepository extends JpaRepository<AuctionImage, Long>{
  List<AuctionImage> findTop1ByAuctionId(Long auctionId);

  @Query("""
      select ai
      from AuctionImage ai
      where ai.id in (
        select min(ai2.id)
        from AuctionImage ai2
        where ai2.auction.id in :auctionIds
        group by ai2.auction.id
      )
      """)
  List<AuctionImage> findFirstImagesByAuctionIds(@Param("auctionIds") List<Long> auctionIds);

  @Query("""
      select
            new com.windfall.api.user.dto.response.reviewlist.AuctionImageRaw(ai.auction.id, ai.image)
      from AuctionImage ai
      where ai.id in (
        select min(ai2.id)
        from AuctionImage ai2
        where ai2.auction.id in :auctionIds
        group by ai2.auction.id
      )
      """)
  List<AuctionImageRaw> findFirstImagesProjection(@Param("auctionIds") List<Long> auctionIds);

  Optional<AuctionImage> findTop1ByAuctionIdOrderByIdAsc(Long auctionId);
}
