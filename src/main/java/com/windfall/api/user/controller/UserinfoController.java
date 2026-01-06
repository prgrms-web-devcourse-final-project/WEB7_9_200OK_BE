package com.windfall.api.user.controller;

import com.windfall.api.user.dto.request.UpdateUsernameRequest;
import com.windfall.api.user.dto.response.UpdateUsernameResponse;
import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.api.user.dto.response.UpdateUserProfileImageResponse;
import com.windfall.api.user.dto.response.reviewlist.ReviewListResponse;
import com.windfall.api.user.dto.response.saleshistory.BaseSalesHistoryResponse;
import com.windfall.api.user.service.UserInfoService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserinfoController implements UserInfoSpecification{

  private final UserInfoService userInfoService;

  @Override
  @GetMapping("/{userid}")
  public ApiResponse<UserInfoResponse> getUserInfo(
      @PathVariable Long userid,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long loginId = null;
    if (userDetails != null) {
      loginId = userDetails.getUserId();
    }

    UserInfoResponse response = userInfoService.getUserInfo(userid, loginId);

    return ApiResponse.ok("사용자 정보 조회에 성공했습니다.", response);
  }

  @Override
  @GetMapping("/{userid}/sales")
  public ApiResponse<SliceResponse<BaseSalesHistoryResponse>> getUserSalesHistory(
      @PathVariable Long userid,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(required = false) String filter,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    Long loginId = null;
    if (userDetails != null) {
      loginId = userDetails.getUserId();
    }

    SliceResponse<BaseSalesHistoryResponse> response = userInfoService.getUserSalesHistory(userid, loginId, filter, pageable);

    return ApiResponse.ok("사용자 판매내역 조회에 성공했습니다.", response);
  }

  @Override
  @GetMapping("/{userId}/reviews")
  public ApiResponse<SliceResponse<ReviewListResponse>> getUserReviewList(
      @PageableDefault(page = 0, size = 10) Pageable pageable,
      @PathVariable Long userId
  ) {

    SliceResponse<ReviewListResponse> response = userInfoService.getUserReviewList(userId, pageable);

    return ApiResponse.ok("사용자 리뷰 목록 조회에 성공하였습니다.", response);
  }

  @Override
  @PutMapping(path = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<UpdateUserProfileImageResponse> updateUserImage(
      @RequestPart(name = "image") MultipartFile file,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){
    Long loginId = userDetails.getUserId();

    UpdateUserProfileImageResponse response = userInfoService.updateUserProfileImage(file, loginId);

    return ApiResponse.ok("사용자 프로필 이미지를 수정하였습니다.", response);
  }

  @Override
  @PutMapping("/names")
  public ApiResponse<UpdateUsernameResponse> updateUsername(
      @RequestBody @Valid UpdateUsernameRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){
    Long loginId = userDetails.getUserId();

    UpdateUsernameResponse response = userInfoService.updateUsername(request, loginId);

    return ApiResponse.ok("사용자 이름을 수정하였습니다.", response);
  }
}
