package com.windfall.api.user.controller;

import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.api.user.dto.response.saleshistory.BaseSalesHistoryResponse;
import com.windfall.api.user.service.UserInfoService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    Long loginId = userDetails.getUserId();

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

    Long loginId = userDetails.getUserId();

    SliceResponse<BaseSalesHistoryResponse> response = userInfoService.getUserSalesHistory(userid, loginId, filter, pageable);

    return ApiResponse.ok("사용자 판매내역 조회에 성공했습니다.", response);
  }
}
