// OAuthKakaoService, OAuthNaverService, OAuthGoogleService가 겹치는 것은 추후 리팩토링 때 정리하겠습니다.
// 각 provider 회사마다 나눈 이유는, 각 회사가 전달하는 데이터 로직이 (미래를 고려해) 미묘하게 달라질 수 있다고 하기 때문입니다.
// 연관 파일
//   RestTemplateConfig.java

package com.windfall.api.user.service;

import com.windfall.api.user.dto.response.LoginUserResponse;
import com.windfall.api.user.dto.response.OAuthUserInfo;
import com.windfall.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuthNaverService {
  private final UserRepository memberRepository;
  //private final JwtService jwtService; // JWT 발급용
  private final RestTemplate restTemplate;

  @Value("${spring.naver.client.id}")
  private String naverClientId;

  @Value("${spring.naver.redirect.uri}")
  private String naverRedirectUri;

  public LoginUserResponse loginOrSignup(String code) {
    // 1. code로 access token 발급
    String accessToken = requestAccessToken(code);

    // 2. access token으로 사용자 정보 요청
    OAuthUserInfo userInfo = requestUserInfo(accessToken);

    // 3. DB에서 회원 확인 후 없으면 생성
    /*
    User member = memberRepository.findByEmail(userInfo.email())
        .orElseGet(() -> memberRepository.save(
            new User(userInfo.email(), userInfo.nickname(), userInfo.profileImageUrl())
        ));
     */

    // 4. JWT 발급
    //String jwtAccessToken = jwtService.generateAccessToken(member);
    //String jwtRefreshToken = jwtService.generateRefreshToken(member);
    String jwtAccessToken = "";
    String jwtRefreshToken = "";

    return new LoginUserResponse("","","");
    //return new LoginUserResponse(member.getUserId(), member.getUsername(), jwtAccessToken, jwtRefreshToken);
  }

  private String requestAccessToken(String code) {
    // RestTemplate 사용, 카카오 API에 POST 요청
    // 반환값은 access token
    return "";
  }

  private OAuthUserInfo requestUserInfo(String accessToken) {
    // RestTemplate 사용, 카카오 API에 GET 요청
    // JSON 응답 → OAuthUserInfo로 변환
    return new OAuthUserInfo("", "", "");
  }
}
