package com.windfall.domain.auction.repository;

import com.windfall.api.auction.dto.response.info.PopularInfo;
import com.windfall.api.auction.dto.response.info.ProcessInfo;
import com.windfall.api.auction.dto.response.info.ScheduledInfo;
import com.windfall.domain.auction.enums.AuctionStatus;
import java.util.List;


public interface AuctionRepositoryCustom {
  List<ProcessInfo> getProcessInfo(AuctionStatus status, int limit);
  List<ScheduledInfo> getScheduledInfo(AuctionStatus status, int limit);
  List<PopularInfo> getPopularInfo(AuctionStatus status, int limit);
}
