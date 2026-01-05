package com.windfall.api.user.controller;

import com.windfall.api.user.dto.request.UpdateUsernameRequest;
import com.windfall.api.user.dto.response.UpdateUserProfileImageResponse;
import com.windfall.api.user.dto.response.UpdateUsernameResponse;
import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.api.user.dto.response.reviewlist.ReviewListResponse;
import com.windfall.api.user.dto.response.saleshistory.BaseSalesHistoryResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.config.swagger.ApiErrorCodes;
import static com.windfall.global.exception.ErrorCode.INVALID_S3_UPLOAD;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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

  @Operation(summary = "사용자 리뷰 목록", description = "특정 사용자의 받은 리뷰를 반환합니다.")
  ApiResponse<SliceResponse<ReviewListResponse>> getUserReviewList(
      @PageableDefault(page = 0, size = 10) Pageable pageable,
      @PathVariable Long userId
  );

  @ApiErrorCodes({INVALID_S3_UPLOAD})
  @Operation(summary = "사용자 프로필 이미지 변경", description = "사용자의 프로필 이미지를 변경합니다.")
  ApiResponse<UpdateUserProfileImageResponse> updateUserImage(
      @RequestPart(name = "image") MultipartFile file,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

  @Operation(summary = "사용자 프로필 이름 변경", description = "사용자의 프로필 이름을 변경합니다.")
  ApiResponse<UpdateUsernameResponse> updateUsername(
      @RequestBody @Valid UpdateUsernameRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

}
