package com.windfall.api.user.controller;

import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "UserInfo", description = "사용자 정보 API")
public interface UserInfoSpecification {

  @Operation(summary = "사용자 정보", description = "특정 사용자의 정보를 반환합니다.")
  ApiResponse<UserInfoResponse> redirectToLogin(@PathVariable Long userid);

}
