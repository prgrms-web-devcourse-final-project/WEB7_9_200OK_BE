package com.windfall.api.user.service;

import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.api.user.dto.response.saleshistory.CompletedSalesHistoryResponse;
import com.windfall.api.user.dto.response.saleshistory.OwnerCompletedSalesHistoryResponse;
import com.windfall.api.user.dto.response.saleshistory.ProcessingSalesHistoryResponse;
import com.windfall.api.user.dto.response.saleshistory.SalesHistoryRaw;
import com.windfall.api.user.dto.response.saleshistory.BaseSalesHistoryResponse;
import com.windfall.api.user.dto.response.saleshistory.SalesHistoryResponse;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.enums.AuctionStatusGroup;
import com.windfall.domain.user.repository.SalesHistoryQueryRepository;
import com.windfall.domain.user.repository.UserRepository;
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
public class UserInfoService {

  private final UserService userService;
  private final UserRepository userRepository;
  private final SalesHistoryQueryRepository salesHistoryQueryRepository;

  @Transactional
  public UserInfoResponse getUserInfo(Long userid, Long loginId){ //userDetails 추가될 경우 리팩토링 예정
    userService.getUserById(userid); //조회할 유저가 있는지 먼저 검색

    return userRepository.findByUserInfo(userid, loginId);
  }

  @Transactional
  public SliceResponse<BaseSalesHistoryResponse> getUserSalesHistory(Long userid, Long loginId,String filter, Pageable pageable){
    userService.getUserById(userid); //조회할 유저가 있는지 먼저 검색

    //1. raw data 추출
    Slice<SalesHistoryRaw> rawData = salesHistoryQueryRepository.getRawSalesHistory(userid, filter, pageable);

    //2. 순서 저장
    List<Long> dataSequence = rawData.stream().map(SalesHistoryRaw::id).toList();

    //3. 분기 처리 (그룹화)
    Map<AuctionStatusGroup, List<Long>> groups = groupingStatus(rawData.getContent(), userid, loginId);

    //4. 분기 별 다른 쿼리 실행
    Map<Long, BaseSalesHistoryResponse> resultData = fetchDetailedData(groups, userid);

    //5. 정렬 조립 작업
    List<BaseSalesHistoryResponse> resultContent = dataSequence.stream().map(resultData::get).toList();

    //6. 다시 slice로 반환
    Slice<BaseSalesHistoryResponse> resultSlice = new SliceImpl<>(
        resultContent,
        rawData.getPageable(),
        rawData.hasNext()
    );

    return SliceResponse.from(resultSlice);
  }

  private Map<AuctionStatusGroup, List<Long>> groupingStatus(List<SalesHistoryRaw> rawData, Long userid, Long loginId){
    Map<AuctionStatusGroup, List<Long>> groups = new HashMap<>(); //분기 처리용 map
    rawData.forEach(raw -> {
      AuctionStatusGroup key = setGroup(raw.status(), userid, loginId);
      groups.computeIfAbsent(key, k -> new ArrayList<>()).add(raw.id());
    });

    return groups;
  }

  private AuctionStatusGroup setGroup(AuctionStatus status, Long userid, Long loginId){ //그룹 설정
    if(status == AuctionStatus.SCHEDULED || status == AuctionStatus.CANCELED) { //group1 : 예정, 취소
      return AuctionStatusGroup.READY_CANCEL;
    }
    if(status == AuctionStatus.PROCESS || status == AuctionStatus.FAILED) { //group2 : 진행 중, 유찰
      return AuctionStatusGroup.PROCESS_FAILED;
    }
    return (userid.equals(loginId)) ? AuctionStatusGroup.COMPLETED_MY : AuctionStatusGroup.COMPLETED_OTHER;
  }

  private Map<Long, BaseSalesHistoryResponse> fetchDetailedData(Map<AuctionStatusGroup, List<Long>> groups, Long userid) { //상태에 따른 분기 처리 및 쿼리 실행
    Map<Long, BaseSalesHistoryResponse> resultData = new HashMap<>();

    if (groups.containsKey(AuctionStatusGroup.READY_CANCEL)) { //쿼리: 준비, 취소 상태
      salesHistoryQueryRepository.getSalesHistory(groups.get(AuctionStatusGroup.READY_CANCEL))
          .forEach(data -> resultData.put(data.get("auctionId", Long.class), SalesHistoryResponse.from(data)));
    }

    if (groups.containsKey(AuctionStatusGroup.PROCESS_FAILED)) { //쿼리: 진행중, 실패 상태
      salesHistoryQueryRepository.getProcessingSalesHistory(groups.get(AuctionStatusGroup.PROCESS_FAILED))
          .forEach(data -> resultData.put(data.get("auctionId", Long.class), ProcessingSalesHistoryResponse.from(data)));
    }

    if (groups.containsKey(AuctionStatusGroup.COMPLETED_MY)) { //쿼리: 낙찰된 경우 (자신)
      salesHistoryQueryRepository.getOwnerCompletedSalesHistory(groups.get(AuctionStatusGroup.COMPLETED_MY), userid)
          .forEach(data -> resultData.put(data.get("auctionId", Long.class), OwnerCompletedSalesHistoryResponse.from(data)));
    }

    if (groups.containsKey(AuctionStatusGroup.COMPLETED_OTHER)) { //쿼리: 낙찰된 경우 (상대방)
      salesHistoryQueryRepository.getCompletedSalesHistory(groups.get(AuctionStatusGroup.COMPLETED_OTHER))
          .forEach(data -> resultData.put(data.get("auctionId", Long.class), CompletedSalesHistoryResponse.from(data)));
    }

    return resultData;
  }

}
