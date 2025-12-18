package com.windfall.domain.auction.repository;

import com.windfall.api.auction.dto.response.AuctionSearchResponse;
import com.windfall.api.auction.dto.response.info.PopularInfo;
import com.windfall.api.auction.dto.response.info.ProcessInfo;
import com.windfall.api.auction.dto.response.info.ScheduledInfo;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface AuctionRepositoryCustom {
  List<ProcessInfo> getProcessInfo(AuctionStatus status, int limit);
  List<ScheduledInfo> getScheduledInfo(AuctionStatus status, int limit);
  List<PopularInfo> getPopularInfo(AuctionStatus status, int limit);

  Slice<AuctionSearchResponse> searchAuction(Pageable pageable,String query, AuctionCategory category, AuctionStatus status, Long minPrice, Long maxPrice);
}
