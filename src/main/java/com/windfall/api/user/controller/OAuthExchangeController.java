package com.windfall.api.user.controller;

import com.windfall.api.user.dto.request.OAuthTokenGoogleRequest;
import com.windfall.api.user.dto.request.OAuthTokenKakaoRequest;
import com.windfall.api.user.dto.request.OAuthTokenNaverRequest;
import com.windfall.api.user.dto.response.OAuthTokenResponse;
import com.windfall.api.user.dto.response.OAuthUserInfo;
import com.windfall.api.user.service.JwtProvider;
import com.windfall.api.user.service.OAuthGoogleService;
import com.windfall.api.user.service.OAuthKakaoService;
import com.windfall.api.user.service.OAuthNaverService;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/exchange")
@RequiredArgsConstructor
public class OAuthExchangeController implements OAuthExchangeSpecification {

  private final JwtProvider jwtProvider;

  //   1. 각 provider에 맞는 서비스 객체 선언.
  private final OAuthKakaoService kakaoService;
  private final OAuthNaverService naverService;
  private final OAuthGoogleService googleService;

  //   2. 카카오로 회원가입/로그인을 담당하고 토큰 스트링을 반환하는 컨트롤러
  @PostMapping("/kakao")
  public ApiResponse<OAuthTokenResponse> kakaoExchange(@RequestBody OAuthTokenKakaoRequest request) {
    String code = request.code();
    String accessToken = kakaoService.requestAccessToken(code);
    OAuthUserInfo userInfo = kakaoService.requestUserInfo(accessToken);
    OAuthTokenResponse OAuthTokenResponse = kakaoService.loginOrSignup(userInfo);

    return ApiResponse.ok("userId, 액세스토큰과 리프레시토큰 반환.", OAuthTokenResponse);
  }

  //   2. 네이버로 회원가입/로그인을 담당하고 토큰 스트링을 반환하는 컨트롤러
  @PostMapping("/naver")
  public ApiResponse<OAuthTokenResponse> naverExchange(@RequestBody OAuthTokenNaverRequest request) {
    String code = request.code();
    String state = request.state();
    String accessToken = naverService.requestAccessToken(code, state);
    OAuthUserInfo userInfo = naverService.requestUserInfo(accessToken);
    OAuthTokenResponse OAuthTokenResponse = naverService.loginOrSignup(userInfo);

    return ApiResponse.ok("userId, 액세스토큰과 리프레시토큰 반환.", OAuthTokenResponse);
  }

  //   2. 구글로 회원가입/로그인을 담당하고 토큰 스트링을 반환하는 컨트롤러
  @PostMapping("/google")
  public ApiResponse<OAuthTokenResponse> googleExchange(@RequestBody OAuthTokenGoogleRequest request) {
    String code = request.code();
    String accessToken = googleService.requestAccessToken(code);
    OAuthUserInfo userInfo = googleService.requestUserInfo(accessToken);
    OAuthTokenResponse OAuthTokenResponse = googleService.loginOrSignup(userInfo);

    return ApiResponse.ok("userId, 액세스토큰과 리프레시토큰 반환.", OAuthTokenResponse);
  }
}
