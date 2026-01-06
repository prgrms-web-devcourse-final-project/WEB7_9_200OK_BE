package com.windfall.api.recentview.service;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.recentview.entity.RecentView;
import com.windfall.domain.recentview.repository.RecentViewRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecentViewService {

  private final RecentViewRepository recentViewRepository;
  private final AuctionRepository auctionRepository;

  @Transactional
  public void record(Long auctionId, Long userId){

    if(!isLoginUser(userId)) return; //비회원이면 해당 로직 실행할 필요가 없음

    Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new ErrorException(
        ErrorCode.NOT_FOUND_AUCTION));

    boolean isRecentViewExists = recentViewRepository.existsByAuctionIdAndUserId(auctionId, userId); //최근 본 목록 기록에 이미 auctionId가 있을 경우

    if(isRecentViewExists){ //있으면 그냥 viewed_at 갱신
      RecentView recentView = recentViewRepository.findByAuctionIdAndUserId(auctionId, userId);
      recentView.updateView();
    }else{ //없으면 새로 생성 및 카운트 후 100개 이상이면 마지막거 제거 후 추가
      removeLastView(userId);
      RecentView recentView = RecentView.create(auction, userId, LocalDateTime.now());
      recentViewRepository.save(recentView);
    }
  }

  @Transactional
  public void deleteView(Long recentViewId, Long userId){
    //최근 본 내역 id가 유효한지 검증
    RecentView recentView = validateRecentView(recentViewId);

    //유저 검증
    validateUser(recentView.getUserId(), userId);

    //삭제
    recentViewRepository.delete(recentView);
  }

  private RecentView validateRecentView(Long recentViewId){
    return recentViewRepository.findById(recentViewId).orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_RECENT_VIEW));
  }

  private void validateUser(Long recentViewUserId, Long userId){
    if(!recentViewUserId.equals(userId)){
      throw new ErrorException(ErrorCode.INVALID_RECENT_VIEW_USERID);
    }
  }

  private void removeLastView(Long userId){
    int count = recentViewRepository.countByUserId(userId);

    if(count >= 100){
      RecentView recentView = recentViewRepository.findTop1ByUserIdOrderByViewedAt(userId);
      recentViewRepository.delete(recentView);
    }
  }

  private boolean isLoginUser(Long userId){
    return userId != null;
  }
}
