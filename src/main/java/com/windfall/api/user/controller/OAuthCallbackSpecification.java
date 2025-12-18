//
// OAuthCallbackSpecification 인터페이스는 OAuthCallbackController 클래스의 Swagger 설명문을 담당합니다.

package com.windfall.api.user.controller;

import com.windfall.api.user.dto.response.LoginUserResponse;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

public interface OAuthCallbackSpecification {

  @Operation(summary = "카카오 로그인 콜백 요청", description = "카카오로 로그인합니다. 한번 회원가입된 아이디로 다시 이 테스트를 하시면 KOE320 오류가 뜹니다. 대신 2번째 로그인부턴 바로 성공 결과가 뜹니다.")
  RedirectView kakaoCallback(@RequestParam String code, HttpServletResponse response);

  @Operation(summary = "네이버 로그인 콜백 요청", description = "네이버로 로그인합니다.")
  ApiResponse<LoginUserResponse> naverCallback(@RequestParam String code, HttpServletResponse response);

  @Operation(summary = "구글 로그인 콜백 요청", description = "구글로 로그인합니다.")
  ApiResponse<LoginUserResponse> googleCallback(@RequestParam String code, HttpServletResponse response);
}
