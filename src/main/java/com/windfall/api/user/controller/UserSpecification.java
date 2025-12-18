//
// UserSpecification 인터페이스는 UserController 클래스의 Swagger 설명문을 담당합니다.

package com.windfall.api.user.controller;

import com.windfall.api.user.dto.response.LoginUserResponse;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User", description = "사용자 API")
public interface UserSpecification {

  @Operation(summary = "로그인 URL 반환", description = "provider에 맞는 로그인 url을 반환합니다.")
  ApiResponse<String> redirectToLogin(@RequestParam String provider);

  @Operation(summary = "로그인 이후 유저 기본 정보 반환",  description = "쿠키 속 액세스 토큰을 받고 해당 사용자에 맞는 이메일, 유저네임, 프로필url을 반환합니다.")
  public ApiResponse<LoginUserResponse> returnBasicUserInfo(@CookieValue("accessToken") String accessToken);

  @Operation(summary = "두 토큰 중 하나라도 사용 가능한가 true/false 반환", description = "쿠키 속 액세스 토큰과 리프레시 토큰 중 하나라도 사용 가능하면 true, 둘 다 만료되었으면 false 반환합니다.")
  public ApiResponse<Boolean> validateTokens(@CookieValue("accessToken") String accessToken, @CookieValue("refreshToken") String refreshToken);
}
