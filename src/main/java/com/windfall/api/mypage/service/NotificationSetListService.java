package com.windfall.api.mypage.service;

import com.windfall.api.mypage.dto.notificationsetlist.BaseNotificationSetList;
import com.windfall.api.mypage.dto.notificationsetlist.CompletedNotificationSetListResponse;
import com.windfall.api.mypage.dto.notificationsetlist.NotificationSetListRaw;
import com.windfall.api.mypage.dto.notificationsetlist.NotificationSetListResponse;
import com.windfall.api.mypage.dto.notificationsetlist.ProcessingNotificationSetListResponse;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.enums.AuctionStatusGroup;
import com.windfall.domain.mypage.repository.NotificationSetListQueryRepository;
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
public class NotificationSetListService {

  private final NotificationSetListQueryRepository notificationSetListQueryRepository;

  @Transactional
  public SliceResponse<BaseNotificationSetList> getMyNotifications(Long userId, AuctionStatus filter, Pageable pageable){
    //1. RawData 추출
    Slice<NotificationSetListRaw> rawData = notificationSetListQueryRepository.getRawNotification(userId, filter, pageable);

    //2. 순서 저장
    List<Long> dataSequence = rawData.getContent().stream().map(NotificationSetListRaw::id).toList();

    //3. 그룹화
    Map<AuctionStatusGroup, List<Long>> groups = groupingData(rawData.getContent());

    //4. 분기 처리
    Map<Long, BaseNotificationSetList> resultData = fetchDetailedData(groups);

    //5. 순서대로 재조립
    List<BaseNotificationSetList> resultContent = orderByResults(dataSequence, resultData);

    //6. 새로운 slice로 반환
    Slice<BaseNotificationSetList> sliceData = toSlice(resultContent, rawData);

    return SliceResponse.from(sliceData);
  }

  private Map<AuctionStatusGroup, List<Long>> groupingData(List<NotificationSetListRaw> rawData){
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

  private Map<Long, BaseNotificationSetList> fetchDetailedData(Map<AuctionStatusGroup, List<Long>> groups){
    Map<Long, BaseNotificationSetList> resultData = new HashMap<>();

    if(groups.containsKey(AuctionStatusGroup.READY_CANCEL)){
      notificationSetListQueryRepository.getNotificationSetList(groups.get(AuctionStatusGroup.READY_CANCEL)).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), NotificationSetListResponse.from(data));
          }
      );
    }
    if(groups.containsKey(AuctionStatusGroup.PROCESS_FAILED)){
      notificationSetListQueryRepository.getProcessingNotificationSetList(groups.get(AuctionStatusGroup.PROCESS_FAILED)).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), ProcessingNotificationSetListResponse.from(data));
          }
      );
    }
    if(groups.containsKey(AuctionStatusGroup.COMPLETED_MY)){
      notificationSetListQueryRepository.getCompletedNotificationSetList(groups.get(AuctionStatusGroup.COMPLETED_MY)).forEach(
          data -> {
            resultData.put(data.get("auctionId", Long.class), CompletedNotificationSetListResponse.from(data));
          }
      );
    }

    return resultData;
  }

  private List<BaseNotificationSetList> orderByResults(List<Long> dataSequence, Map<Long, BaseNotificationSetList> resultData){
    return dataSequence.stream().map(resultData::get).toList();
  }

  private Slice<BaseNotificationSetList> toSlice(List<BaseNotificationSetList> resultContent, Slice<?> rawData){
    return new SliceImpl<>(
        resultContent,
        rawData.getPageable(),
        rawData.hasNext()
    );
  }

}
