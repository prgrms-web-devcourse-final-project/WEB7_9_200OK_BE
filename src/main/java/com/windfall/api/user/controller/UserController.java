//
// UserController.java
// 이 파일은 사용자의 OAuth 로그인 시작을 담당합니다.
// 이 파일은 아래와 같은 구조로 이루어져 있습니다.
//   1. 로그인 화면 요청 url에 필요한 값들을 application.yml에서 가져옵니다.
//   2. 로그인 화면 요청 url에 쓸 변수를 선언합니다.
//   3. init()함수로 로그인 화면 요청 url을 조합합니다.
//   4. redirectToLogin 함수에서 provider에 맞는 url을 반환합니다.
//   5. 이를 통해 사용자는 provider에 맞는 로그인 페이지로 이동합니다.
// provider란?: 카카오, 구글, 네이버 등 리소스 서버(연동사)를 지칭하는 말.

package com.windfall.api.user.controller;

import com.windfall.api.user.dto.response.LoginUserResponse;
import com.windfall.api.user.dto.response.OAuthTokenResponse;
import com.windfall.api.user.service.JwtProvider;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.user.entity.User;
import com.windfall.global.response.ApiResponse;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController implements UserSpecification {

  private final UserService userService;
  private final JwtProvider jwtProvider;

  // 1. 로그인 화면 요청 url에 필요한 값들. application.yml 참고.
  @Value("${spring.kakao.client.id}")
  private String kakaoRestApiKey;
  @Value("${spring.kakao.redirect.uri}")
  private String kakaoRedirectUri;

  @Value("${spring.naver.client.id}")
  private String naverClientId;
  @Value("${spring.naver.redirect.uri}")
  private String naverRedirectUri;

  @Value("${spring.google.client.id}")
  private String googleClientId;
  @Value("${spring.google.redirect.uri}")
  private String googleRedirectUri;

  // 2. 로그인 화면 요청 url String.
  private String URL_KAKAO = "";
  private String URL_NAVER = "";
  private String URL_GOOGLE = "";
  private String URL_WRONG = "";

  // 3. url String을 @Value 주입 후에 실행되게 함.
  @PostConstruct
  private void init() {
    URL_KAKAO = "https://kauth.kakao.com/oauth/authorize"
        + "?client_id=" + kakaoRestApiKey
        + "&redirect_uri=" + kakaoRedirectUri
        + "&response_type=code";

    URL_NAVER = "https://nid.naver.com/oauth2.0/authorize"
        + "?client_id=" + naverClientId
        + "&redirect_uri=" + naverRedirectUri
        + "&response_type=code";

    URL_GOOGLE = "https://accounts.google.com/o/oauth2/v2/auth"
        + "?client_id=" + googleClientId
        + "&redirect_uri=" + googleRedirectUri
        + "&response_type=code"
        + "&scope=openid%20email%20profile";
  }

  // 4. 로그인 화면 요청과 반환하는 컨트롤러
  @GetMapping("/auth")
  public ApiResponse<String> redirectToLogin(@RequestParam String provider) {

    return switch (provider.toLowerCase()) {
      case "kakao" -> ApiResponse.ok(provider + " 로그인 페이지.", URL_KAKAO);
      case "naver" -> ApiResponse.ok(provider + " 로그인 페이지.", URL_NAVER);
      case "google" -> ApiResponse.ok(provider + " 로그인 페이지.", URL_GOOGLE);
      default -> ApiResponse.ok(provider + "는 잘못된 provider입니다.", URL_WRONG);
    };
  }

  //
  @GetMapping("/auth/basic")
  public ApiResponse<LoginUserResponse> returnBasicUserInfo(
      @CookieValue String accessToken) {

    User user = userService.getUserByProviderUserId(jwtProvider.getProviderUserId(accessToken));
    return ApiResponse.ok("유저 기본 정보가 반환되었습니다.",
        new LoginUserResponse(user.getEmail(), user.getNickname(), user.getProfileImageUrl()));
  }

  @GetMapping("/auth/validate-tokens")
  public ApiResponse<Boolean> validateTokens(@CookieValue("accessToken") String accessToken,
      @CookieValue("refreshToken") String refreshToken) {

    boolean isAccessValid = jwtProvider.validateToken(accessToken);
    boolean isRefreshValid = jwtProvider.validateToken(refreshToken);

    if (isAccessValid || isRefreshValid) {
      return ApiResponse.ok("최소 한 토큰은 정상입니다.", true);
    } else {
      return ApiResponse.ok("두 토큰 모두 만료되었습니다.", false);
    }
  }

  @PostMapping("/auth/regenerate-access-token")
  public ApiResponse<OAuthTokenResponse> regenerateAccessToken(HttpServletRequest request) {

    OAuthTokenResponse oAuthTokenResponse = userService.regenerateAccessToken(request);
    return ApiResponse.ok("액세스 토큰 재발급 완료", oAuthTokenResponse);
  }


  private Cookie generateCookieWithAccessToken(String token) {
    Cookie accessTokenCookie = new Cookie("accessToken", token);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setSecure(true);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(1 * 1 * 60 * 60); // 1시간
    return accessTokenCookie;
  }
}
