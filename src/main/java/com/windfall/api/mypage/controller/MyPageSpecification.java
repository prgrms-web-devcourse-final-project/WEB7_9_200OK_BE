package com.windfall.api.mypage.controller;

import com.windfall.api.mypage.dto.purchasehistory.BasePurchaseHistory;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "MyPage", description = "마이페이지 API")
public interface MyPageSpecification {

  @Operation(summary = "나의 구매내역 조회", description = "자신의 구매내역을 조회합니다.")
  ApiResponse<SliceResponse<BasePurchaseHistory>> getMyPurchaseHistory(@PageableDefault(page = 0, size = 10) Pageable pageable,
      @RequestParam(required = false) String filter,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );
}
