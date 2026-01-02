package com.windfall.api.mypage.service;

import com.windfall.api.mypage.dto.dashboard.BaseDashBoardDetails;
import com.windfall.api.mypage.dto.dashboard.DashBoardDetailsRaw;
import com.windfall.api.mypage.dto.dashboard.DashBoardDetailsResponse;
import com.windfall.api.mypage.dto.dashboard.ProcessingDashBoardDetailsResponse;
import com.windfall.api.mypage.dto.recentviewlist.BaseRecentViewList;
import com.windfall.api.mypage.dto.recentviewlist.CompletedRecentViewListResponse;
import com.windfall.api.mypage.dto.recentviewlist.ProcessingRecentViewListResponse;
import com.windfall.api.mypage.dto.recentviewlist.RecentViewListRaw;
import com.windfall.api.mypage.dto.recentviewlist.RecentViewListResponse;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.enums.AuctionStatusGroup;
import com.windfall.domain.mypage.repository.DashBoardDetailsQueryRepository;
import com.windfall.global.response.SliceResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashBoardDetailsService {

  private final DashBoardDetailsQueryRepository dashBoardDetailsQueryRepository;

  @Transactional(readOnly = true)
  public SliceResponse<BaseDashBoardDetails> getDashBoardDetails(LocalDate date, AuctionStatus filter, Pageable pageable){
    LocalDateTime todayStart = date.atStartOfDay(); //ex. 2026-01-01 00:00:00
    LocalDateTime todayEnd = date.plusDays(1).atStartOfDay(); //ex. 2026-01-02 00:00:00

    //1. RawData 추출
    Slice<DashBoardDetailsRaw> rawData = dashBoardDetailsQueryRepository.getRawDashBoardDetails(todayStart, todayEnd, filter, pageable);

    //2. 순서 저장
    List<Long> dataSequence = rawData.getContent().stream().map(DashBoardDetailsRaw::id).toList();

    //3. 그룹화
    Map<AuctionStatus, List<Long>> groups = groupingData(rawData.getContent());

    //4. 분기 처리
    Map<Long, BaseDashBoardDetails> resultData = fetchDetailedData(groups);

    //5. 순서대로 재조립
    List<BaseDashBoardDetails> resultContent = orderByResults(dataSequence, resultData);

    //6. 새로운 slice로 반환
    Slice<BaseDashBoardDetails> sliceData = toSlice(resultContent, rawData);

    return SliceResponse.from(sliceData);
  }

  private Map<AuctionStatus, List<Long>> groupingData(List<DashBoardDetailsRaw> rawData){
    Map<AuctionStatus, List<Long>> resultData = new HashMap<>();

    rawData.forEach(raw -> {
      AuctionStatus key = raw.status();
      resultData.computeIfAbsent(key, k -> new ArrayList<>()).add(raw.id());
    });

    return resultData;
  }

  private Map<Long, BaseDashBoardDetails> fetchDetailedData(Map<AuctionStatus, List<Long>> groups){
    Map<Long, BaseDashBoardDetails> resultData = new HashMap<>();

    if(groups.containsKey(AuctionStatus.SCHEDULED)){
      dashBoardDetailsQueryRepository.getDashBoardDetails(groups.get(AuctionStatus.SCHEDULED)).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), DashBoardDetailsResponse.from(data));
          }
      );
    }
    if(groups.containsKey(AuctionStatus.PROCESS)){
      dashBoardDetailsQueryRepository.getProcessingDashBoardDetails(groups.get(AuctionStatus.PROCESS)).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), ProcessingDashBoardDetailsResponse.from(data));
          }
      );
    }

    return resultData;
  }

  private List<BaseDashBoardDetails> orderByResults(List<Long> dataSequence, Map<Long, BaseDashBoardDetails> resultData){
    return dataSequence.stream().map(resultData::get).toList();
  }

  private Slice<BaseDashBoardDetails> toSlice(List<BaseDashBoardDetails> resultContent, Slice<?> rawData){
    return new SliceImpl<>(
        resultContent,
        rawData.getPageable(),
        rawData.hasNext()
    );
  }

}
