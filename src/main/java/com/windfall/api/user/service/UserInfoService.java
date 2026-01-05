package com.windfall.api.user.service;

import com.windfall.api.user.dto.request.UpdateUsernameRequest;
import com.windfall.api.user.dto.response.UpdateUsernameResponse;
import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.api.user.dto.response.UpdateUserProfileImageResponse;
import com.windfall.api.user.dto.response.reviewlist.AuctionImageRaw;
import com.windfall.api.user.dto.response.reviewlist.ReviewListRaw;
import com.windfall.api.user.dto.response.reviewlist.ReviewListResponse;
import com.windfall.api.user.dto.response.saleshistory.CompletedSalesHistoryResponse;
import com.windfall.api.user.dto.response.saleshistory.OwnerCompletedSalesHistoryResponse;
import com.windfall.api.user.dto.response.saleshistory.ProcessingSalesHistoryResponse;
import com.windfall.api.user.dto.response.saleshistory.SalesHistoryRaw;
import com.windfall.api.user.dto.response.saleshistory.BaseSalesHistoryResponse;
import com.windfall.api.user.dto.response.saleshistory.SalesHistoryResponse;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.enums.AuctionStatusGroup;
import com.windfall.domain.auction.repository.AuctionImageRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.repository.SalesHistoryQueryRepository;
import com.windfall.domain.user.repository.UserInfoRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import com.windfall.global.response.SliceResponse;
import com.windfall.global.s3.S3Uploader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserInfoService {

  private final UserService userService;
  private final UserInfoRepository userInfoRepository;
  private final SalesHistoryQueryRepository salesHistoryQueryRepository;
  private final AuctionImageRepository auctionImageRepository;
  private final S3Uploader s3Uploader;

  @Transactional
  public UserInfoResponse getUserInfo(Long userid, Long loginId){ //userDetails 추가될 경우 리팩토링 예정
    userService.getUserById(userid); //조회할 유저가 있는지 먼저 검색

    return userInfoRepository.findByUserInfo(userid, loginId);
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

  @Transactional(readOnly = true)
  public SliceResponse<ReviewListResponse> getUserReviewList(Long userId, Pageable pageable){
    userService.getUserById(userId); //조회할 유저가 있는지 먼저 검색

    Slice<ReviewListRaw> rawData = userInfoRepository.getUserReviewList(userId, pageable); //필요 데이터 꺼내오기 (이미지 제외)

    List<Long> auctionIds = rawData.getContent().stream().map(ReviewListRaw::auctionId).toList(); //데이터 내부의 auctionId 추출

    List<AuctionImageRaw> auctionImages = auctionImageRepository.findFirstImagesProjection(auctionIds); //각 경매의 첫 번째 이미지 추출

    Map<Long, String> mappingImage = auctionImages.stream().collect(Collectors.toMap(
        AuctionImageRaw::auctionId, //매핑용 이미지 설정
        AuctionImageRaw::auctionImageUrl));

    List<ReviewListResponse> resultContent = rawData.getContent().stream().map(data -> ReviewListResponse.of(data, mappingImage.get(data.auctionId()))).toList(); //순서대로 DTO 변환

    Slice<ReviewListResponse> resultSlice = toSlice(resultContent, rawData); //Slice 재정의

    return SliceResponse.from(resultSlice);
  }

  @Transactional
  public UpdateUserProfileImageResponse updateUserProfileImage(MultipartFile image, Long loginId){
    User user = userService.getUserById(loginId);

    validateImage(image);

    String oldUrl = user.getProfileImageUrl();
    String dirName = "profile/" + loginId;
    String newUrl = s3Uploader.upload(image, dirName);

    user.updateProfileImage(newUrl); //S3에 새로운 이미지 저장

    if(oldUrl != null){ //S3에 기존 이미지 삭제
      String key = URIPathParser(oldUrl);
      s3Uploader.deleteFile(key);
    }
    return new UpdateUserProfileImageResponse(newUrl);
  }

  @Transactional
  public UpdateUsernameResponse updateUsername(UpdateUsernameRequest request, Long loginId){
    User user = userService.getUserById(loginId);

    user.updateUsername(request.username());

    return new UpdateUsernameResponse(request.username());
  }

  private void validateImage(MultipartFile files) {
    if (files == null || files.isEmpty()) {
      throw new ErrorException(ErrorCode.INVALID_IMAGE_FILE);
    }
  }

  private String URIPathParser(String url){
    URI uri = URI.create(url);
    return uri.getPath().substring(1);
  }

  private Slice<ReviewListResponse> toSlice (List<ReviewListResponse> content, Slice<ReviewListRaw> sliceData){
    return new SliceImpl<>(
        content,
        sliceData.getPageable(),
        sliceData.hasNext());
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
