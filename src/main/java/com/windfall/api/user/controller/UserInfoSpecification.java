package com.windfall.api.user.controller;

import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.api.user.dto.response.saleshistory.BaseSalesHistoryResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "UserInfo", description = "사용자 정보 API")
public interface UserInfoSpecification {

  @Operation(summary = "사용자 정보", description = "특정 사용자의 정보를 반환합니다.")
  ApiResponse<UserInfoResponse> getUserInfo(
      @PathVariable Long userid,
      @AuthenticationPrincipal
      CustomUserDetails userDetails);

  @Operation(summary = "사용자 판매 내역", description = "특정 사용자의 판매 내역을 반환합니다.")
  ApiResponse<SliceResponse<BaseSalesHistoryResponse>> getUserSalesHistory(
      @PathVariable Long userid,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(required = false) String filter,
      @PageableDefault(page = 0, size = 10) Pageable pageable

  );

}
