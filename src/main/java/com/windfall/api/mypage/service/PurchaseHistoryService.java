package com.windfall.api.mypage.service;

import com.windfall.api.mypage.dto.purchasehistory.BasePurchaseHistory;
import com.windfall.api.mypage.dto.purchasehistory.ConfirmedPurchaseHistoryResponse;
import com.windfall.api.mypage.dto.purchasehistory.PurchaseGroupsDTO;
import com.windfall.api.mypage.dto.purchasehistory.PurchaseHistoryRaw;
import com.windfall.api.mypage.dto.purchasehistory.PurchaseHistoryResponse;
import com.windfall.domain.mypage.repository.PurchaseHistoryQueryRepository;
import com.windfall.domain.trade.enums.TradeStatus;
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
public class PurchaseHistoryService {

  private final PurchaseHistoryQueryRepository purchaseHistoryQueryRepository;

  @Transactional
  public SliceResponse<BasePurchaseHistory> getPurchaseHistories(Long myId, String filter, Pageable pageable){

    //1. rawdata 추출
    Slice<PurchaseHistoryRaw> rawData = purchaseHistoryQueryRepository.getRawPurchaseHistory(myId, filter, pageable);

    //2. 순서 저장
    List<Long> dataSequence = rawData.stream().map(PurchaseHistoryRaw::auctionId).toList();

    //3. 분기 작업 (그룹화)
    PurchaseGroupsDTO groups = groupingData(rawData.getContent());

    //4. 상태 별 쿼리 실행
    Map<Long, BasePurchaseHistory> resultData = fetchDetailedData(groups, myId);

    //5. 순서 재조립
    List<BasePurchaseHistory> resultContent = orderByResults(dataSequence, resultData);

    //6. 다시 slice로 반환
    Slice<BasePurchaseHistory> resultSlice = toSlice(resultContent, rawData);

    return SliceResponse.from(resultSlice);
  }

  private PurchaseGroupsDTO groupingData(List<PurchaseHistoryRaw> rawData){
    Map<TradeStatus, List<Long>> tradeGroups = new HashMap<>();
    Map<TradeStatus, List<Long>> auctionGroups = new HashMap<>();
    rawData.forEach(raw ->
    {
      tradeGroups.computeIfAbsent(raw.status(), k -> new ArrayList<>()).add(raw.tradeId()); //이거까지 담는 이유: trade 상태별로 쿼리를 실행해야하기 때문에
      auctionGroups.computeIfAbsent(raw.status(), k -> new ArrayList<>()).add(raw.auctionId());
    });

    return new PurchaseGroupsDTO(tradeGroups, auctionGroups);
  }

  private Map<Long, BasePurchaseHistory> fetchDetailedData(PurchaseGroupsDTO groups, Long userid){
    Map<Long, BasePurchaseHistory> resultData = new HashMap<>();
    Map<TradeStatus, List<Long>> tradeGroups = groups.tradeGroups();
    Map<TradeStatus, List<Long>> auctionGroups = groups.auctionGroups();

    System.out.println(tradeGroups.get(TradeStatus.PAYMENT_COMPLETED));

    if(tradeGroups.containsKey(TradeStatus.PAYMENT_COMPLETED)){ //결제 완료
      purchaseHistoryQueryRepository.getPurchaseHistory(userid, tradeGroups.get(TradeStatus.PAYMENT_COMPLETED), auctionGroups.get(TradeStatus.PAYMENT_COMPLETED)).forEach(
      data -> resultData.put(data.get("auctionId", Long.class), PurchaseHistoryResponse.from(data)));
    }
    if(tradeGroups.containsKey(TradeStatus.PURCHASE_CONFIRMED)){ //구매 확정
      purchaseHistoryQueryRepository.getConfirmedPurchaseHistory(userid, tradeGroups.get(TradeStatus.PURCHASE_CONFIRMED), auctionGroups.get(TradeStatus.PURCHASE_CONFIRMED)).forEach(
          data -> resultData.put(data.get("auctionId", Long.class), ConfirmedPurchaseHistoryResponse.from(data)));
    }

    return resultData;
  }

  private List<BasePurchaseHistory> orderByResults(List<Long> dataSequence, Map<Long, BasePurchaseHistory> resultData){
    return dataSequence.stream().map(resultData::get).toList();
  }

  private Slice<BasePurchaseHistory> toSlice(List<BasePurchaseHistory> resultContent, Slice<?> rawData){
    return new SliceImpl<>(
        resultContent,
        rawData.getPageable(),
        rawData.hasNext()
    );
  }
}
