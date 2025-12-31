package com.windfall.api.mypage.service;

import com.windfall.api.mypage.dto.auctionlikelist.AuctionLikeListRaw;
import com.windfall.api.mypage.dto.auctionlikelist.AuctionLikeListResponse;
import com.windfall.api.mypage.dto.auctionlikelist.BaseAuctionLikeList;
import com.windfall.api.mypage.dto.auctionlikelist.CompletedAuctionLikeListResponse;
import com.windfall.api.mypage.dto.auctionlikelist.ProcessingAuctionLikeListResponse;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.enums.AuctionStatusGroup;
import com.windfall.domain.mypage.repository.AuctionLikeListQueryRepository;
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
public class AuctionLikeListService {

  private final AuctionLikeListQueryRepository auctionLikeListQueryRepository;

  @Transactional
  public SliceResponse<BaseAuctionLikeList> getMyAuctionLikes(Long userId, AuctionStatus filter, Pageable pageable){
    //1. RawData 추출
    Slice<AuctionLikeListRaw> rawData = auctionLikeListQueryRepository.getRawAuctionLikeList(userId, filter, pageable);

    //2. 순서 저장
    List<Long> dataSequence = rawData.getContent().stream().map(AuctionLikeListRaw::id).toList();

    //3. 그룹화
    Map<AuctionStatusGroup, List<Long>> groups = groupingData(rawData.getContent());

    //4. 분기 처리
    Map<Long, BaseAuctionLikeList> resultData = fetchDetailedData(groups, userId);

    //5. 순서대로 재조립
    List<BaseAuctionLikeList> resultContent = orderByResults(dataSequence, resultData);

    //6. 새로운 slice로 반환
    Slice<BaseAuctionLikeList> sliceData = toSlice(resultContent, rawData);

    return SliceResponse.from(sliceData);
  }

  private Map<AuctionStatusGroup, List<Long>> groupingData(List<AuctionLikeListRaw> rawData){
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

  private Map<Long, BaseAuctionLikeList> fetchDetailedData(Map<AuctionStatusGroup, List<Long>> groups, Long userId){
    Map<Long, BaseAuctionLikeList> resultData = new HashMap<>();

    if(groups.containsKey(AuctionStatusGroup.READY_CANCEL)){
      auctionLikeListQueryRepository.getAuctionLikeList(groups.get(AuctionStatusGroup.READY_CANCEL), userId).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), AuctionLikeListResponse.from(data));
          }
      );
    }
    if(groups.containsKey(AuctionStatusGroup.PROCESS_FAILED)){
      auctionLikeListQueryRepository.getProcessingAuctionLikeList(groups.get(AuctionStatusGroup.PROCESS_FAILED), userId).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), ProcessingAuctionLikeListResponse.from(data));
          }
      );
    }
    if(groups.containsKey(AuctionStatusGroup.COMPLETED_MY)){
      auctionLikeListQueryRepository.getCompletedAuctionLikeList(groups.get(AuctionStatusGroup.COMPLETED_MY), userId).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), CompletedAuctionLikeListResponse.from(data));
          }
      );
    }

    return resultData;
  }

  private List<BaseAuctionLikeList> orderByResults(List<Long> dataSequence, Map<Long, BaseAuctionLikeList> resultData){
    return dataSequence.stream().map(resultData::get).toList();
  }

  private Slice<BaseAuctionLikeList> toSlice(List<BaseAuctionLikeList> resultContent, Slice<?> rawData){
    return new SliceImpl<>(
        resultContent,
        rawData.getPageable(),
        rawData.hasNext()
    );
  }

}
