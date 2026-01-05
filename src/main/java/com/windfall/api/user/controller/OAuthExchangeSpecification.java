package com.windfall.api.user.controller;

import com.windfall.api.user.dto.request.OAuthTokenKakaoRequest;
import com.windfall.api.user.dto.response.OAuthTokenResponse;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;

public interface OAuthExchangeSpecification {
  @Operation(summary = "카카오 로그인 익스체인지 요청", description = "카카오 콜백에서 받은 코드로 요청하면 액세스토큰, 리프레시토큰 스트링을 반환합니다.")
  ApiResponse<OAuthTokenResponse> kakaoExchange(@RequestBody OAuthTokenKakaoRequest request);
}
