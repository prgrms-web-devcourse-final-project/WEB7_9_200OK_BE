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

/*
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
  public RedirectView kakaoCallback(
      @RequestParam String code,
      HttpServletResponse response
  ) {
    RegisterUserResponse registerUserResponse = kakaoService.loginOrSignup(code);

    ResponseCookie accessTokenCookie = ResponseCookie.from(
            "accessToken", registerUserResponse.accessToken())
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(60 * 60)
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from(
            "refreshToken", registerUserResponse.refreshToken())
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(7 * 24 * 60 * 60)
        .build();

    response.addHeader("Set-Cookie", accessTokenCookie.toString());
    response.addHeader("Set-Cookie", refreshTokenCookie.toString());

    RedirectView redirectView = new RedirectView();
    redirectView.setUrl("https://windfall-auction.vercel.app");
    redirectView.setExposeModelAttributes(false);
    return redirectView;
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

}*/