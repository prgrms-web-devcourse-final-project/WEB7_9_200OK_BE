//
// OAuthCallbackController.java
// 이 컨트롤러는 유저에게 실제 로그인을 담당합니다.
// 이 파일은 아래와 같은 구조로 이루어져 있습니다.
//   1. 각 provider에 맞는 서비스 객체 선언.
//   2. 카카오로 회원가입/로그인하는 것을 담당하는 컨트롤러.
//   3. 네이버로 회원가입/로그인하는 것을 담당하는 컨트롤러.
//   4. 구글로 회원가입/로그인하는 것을 담당하는 컨트롤러.
// provider란?: 카카오, 구글, 네이버 등 리소스 서버(연동사)를 지칭하는 말.

package com.windfall.api.user.controller;

import com.windfall.api.user.dto.response.LoginUserResponse;
import com.windfall.api.user.dto.response.RegisterUserResponse;
import com.windfall.api.user.service.OAuthGoogleService;
import com.windfall.api.user.service.OAuthKakaoService;
import com.windfall.api.user.service.OAuthNaverService;
import com.windfall.global.response.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/callback")
@RequiredArgsConstructor
public class OAuthCallbackController implements OAuthCallbackSpecification {

  //   1. 각 provider에 맞는 서비스 객체 선언.
  private final OAuthKakaoService kakaoService;
  private final OAuthNaverService naverService;
  private final OAuthGoogleService googleService;


  //   2. 카카오로 회원가입/로그인하는 것을 담당하는 컨트롤러
  @GetMapping("/kakao")
  public ApiResponse<LoginUserResponse> kakaoCallback(
      @RequestParam String code, HttpServletResponse response
  ) {
    // kakaoService에서 code로 access token 요청, 사용자 정보 가져오기
    RegisterUserResponse registerUserResponse = kakaoService.loginOrSignup(code);
    LoginUserResponse loginUserResponse = new LoginUserResponse(
        registerUserResponse.userEmail(),
        registerUserResponse.userNickname(),
        registerUserResponse.userProfileUrl());

    response.addCookie(generateCookieWithAccessToken(registerUserResponse.accessToken()));
    response.addCookie(generateCookieWithRefreshToken(registerUserResponse.refreshToken()));
    return ApiResponse.ok("카카오 로그인 성공", loginUserResponse);
  }

  //   3. 네이버로 회원가입/로그인하는 것을 담당하는 컨트롤러
  @GetMapping("/naver")
  public ApiResponse<LoginUserResponse> naverCallback(
      @RequestParam String code, HttpServletResponse response
  ) {
    // naverService에서 code로 access token 요청, 사용자 정보 가져오기
    LoginUserResponse loginUserResponse = naverService.loginOrSignup(code);
    response.addCookie(generateCookieWithAccessToken(""));
    response.addCookie(generateCookieWithRefreshToken(""));
    return ApiResponse.ok("네이버 로그인 성공", loginUserResponse);
  }

  //   4. 구글로 회원가입/로그인하는 것을 담당하는 컨트롤러
  @GetMapping("/google")
  public ApiResponse<LoginUserResponse> googleCallback(
      @RequestParam String code, HttpServletResponse response
  ) {
    // googleService에서 code로 access token 요청, 사용자 정보 가져오기
    LoginUserResponse loginUserResponse = googleService.loginOrSignup(code);
    response.addCookie(generateCookieWithAccessToken(""));
    response.addCookie(generateCookieWithRefreshToken(""));
    return ApiResponse.ok("구글 로그인 성공", loginUserResponse);
  }

  private Cookie generateCookieWithAccessToken(String token) {
    Cookie accessTokenCookie = new Cookie("accessToken", token);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setSecure(true);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(1 * 1 * 60 * 60); // 1시간
    return accessTokenCookie;
  }

  private Cookie generateCookieWithRefreshToken(String token) {
    Cookie refreshTokenCookie = new Cookie("refreshToken", token);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 (일주일)
    return refreshTokenCookie;
  }

}