package com.windfall.api.user.controller;

import com.windfall.api.user.dto.response.LoginUserResponse;
import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.api.user.service.UserInfoService;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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
      @RequestParam(defaultValue = "1") Long loginId) {

    UserInfoResponse response = userInfoService.getUserInfo(userid, loginId);

    return ApiResponse.ok("사용자 정보 조회에 성공했습니다.", null);
  }
}
