package com.windfall.api.mypage.service;


import com.windfall.api.mypage.dto.recentviewlist.BaseRecentViewList;
import com.windfall.api.mypage.dto.recentviewlist.CompletedRecentViewListResponse;
import com.windfall.api.mypage.dto.recentviewlist.ProcessingRecentViewListResponse;
import com.windfall.api.mypage.dto.recentviewlist.RecentViewListRaw;
import com.windfall.api.mypage.dto.recentviewlist.RecentViewListResponse;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.enums.AuctionStatusGroup;
import com.windfall.domain.mypage.repository.RecentViewListQueryRepository;
import com.windfall.global.response.SliceResponse;
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
public class RecentViewListService {
  
  private final RecentViewListQueryRepository recentViewListQueryRepository;
  
  @Transactional
  public SliceResponse<BaseRecentViewList> getMyRecentViewLists(Long userId, AuctionStatus filter, Pageable pageable){
    //1. RawData 추출
    Slice<RecentViewListRaw> rawData = recentViewListQueryRepository.getRawRecentViewList(userId, filter, pageable);

    //2. 순서 저장
    List<Long> dataSequence = rawData.getContent().stream().map(RecentViewListRaw::id).toList();

    //3. 그룹화
    Map<AuctionStatusGroup, List<Long>> groups = groupingData(rawData.getContent());

    //4. 분기 처리
    Map<Long, BaseRecentViewList> resultData = fetchDetailedData(groups, userId);

    //5. 순서대로 재조립
    List<BaseRecentViewList> resultContent = orderByResults(dataSequence, resultData);

    //6. 새로운 slice로 반환
    Slice<BaseRecentViewList> sliceData = toSlice(resultContent, rawData);

    return SliceResponse.from(sliceData);
  }

  private Map<AuctionStatusGroup, List<Long>> groupingData(List<RecentViewListRaw> rawData){
    Map<AuctionStatusGroup, List<Long>> resultData = new HashMap<>();

    rawData.forEach(raw -> {
      AuctionStatusGroup key = setGroup(raw.status());
      resultData.computeIfAbsent(key, k -> new ArrayList<>()).add(raw.id());
    });

    return resultData;
  }

  private AuctionStatusGroup setGroup(AuctionStatus status){
    if(status == AuctionStatus.SCHEDULED || status == AuctionStatus.CANCELED) { //예정, 취소
      return AuctionStatusGroup.READY_CANCEL;
    }
    if(status == AuctionStatus.PROCESS || status == AuctionStatus.FAILED) { //진행 중, 유찰
      return AuctionStatusGroup.PROCESS_FAILED;
    }
    return AuctionStatusGroup.COMPLETED_MY;
  }

  private Map<Long, BaseRecentViewList> fetchDetailedData(Map<AuctionStatusGroup, List<Long>> groups, Long userId){
    Map<Long, BaseRecentViewList> resultData = new HashMap<>();

    if(groups.containsKey(AuctionStatusGroup.READY_CANCEL)){
      recentViewListQueryRepository.getRecentViewList(groups.get(AuctionStatusGroup.READY_CANCEL), userId).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), RecentViewListResponse.from(data));
          }
      );
    }
    if(groups.containsKey(AuctionStatusGroup.PROCESS_FAILED)){
      recentViewListQueryRepository.getProcessingRecentViewList(groups.get(AuctionStatusGroup.PROCESS_FAILED), userId).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), ProcessingRecentViewListResponse.from(data));
          }
      );
    }
    if(groups.containsKey(AuctionStatusGroup.COMPLETED_MY)){
      recentViewListQueryRepository.getCompletedRecentViewList(groups.get(AuctionStatusGroup.COMPLETED_MY), userId).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), CompletedRecentViewListResponse.from(data));
          }
      );
    }

    return resultData;
  }

  private List<BaseRecentViewList> orderByResults(List<Long> dataSequence, Map<Long, BaseRecentViewList> resultData){
    return dataSequence.stream().map(resultData::get).toList();
  }

  private Slice<BaseRecentViewList> toSlice(List<BaseRecentViewList> resultContent, Slice<?> rawData){
    return new SliceImpl<>(
        resultContent,
        rawData.getPageable(),
        rawData.hasNext()
    );
  }
  
}
