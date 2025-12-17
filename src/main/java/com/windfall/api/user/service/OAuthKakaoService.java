// OAuthKakaoService, OAuthNaverService, OAuthGoogleService가 겹치는 것은 추후 리팩토링 때 정리하겠습니다.
// 각 provider 회사마다 나눈 이유는, 각 회사가 전달하는 데이터 로직이 (미래를 고려해) 미묘하게 달라질 수 있다고 하기 때문입니다.
// 연관 파일
//   RestTemplateConfig.java

package com.windfall.api.user.service;

import com.windfall.api.user.dto.response.LoginUserResponse;
import com.windfall.api.user.dto.response.OAuthUserInfo;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.domain.user.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuthKakaoService {

  private final UserRepository userRepository;
  private final JwtProvider jwtProvider; // JWT 발급용
  private final RestTemplate restTemplate;

  @Value("${spring.kakao.client.id}")
  private String kakaoClientId;

  @Value("${spring.kakao.redirect.uri}")
  private String kakaoRedirectUri;

  public LoginUserResponse loginOrSignup(String code) {
    System.out.println("파라미터로 받은 코드: " + code);
    // 0. User 객체 생성. 내부 값 채우기. providerUserId와 email은 어디서 얻어오지?

    // 1. code로 access token 발급
    String accessToken = requestAccessToken(code);

    // 2. access token으로 사용자 정보 요청
    OAuthUserInfo userInfo = requestUserInfo(accessToken);

    // 3. DB에서 회원 확인 후 없으면 생성
    User user = userRepository.findByProviderUserId(userInfo.providerUserId()).orElseGet(() -> userRepository.save(
        new User(ProviderType.KAKAO, userInfo.providerUserId(), userInfo.email(), userInfo.nickname(),
            userInfo.profileImageUrl())
    ));

    return new LoginUserResponse(user.getEmail(), user.getNickname(), user.getProfileImageUrl());
  }

  private String requestAccessToken(String code) {
    String url = "https://kauth.kakao.com/oauth/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", kakaoClientId);
    params.add("redirect_uri", kakaoRedirectUri);
    // 최근 정책 추가로 secret도 넣기 (카카오만) -> 공식문서와 알림 팝업 읽기를 생활화하자...
    params.add("client_secret", "Cw56KE9EtHsaoTAkaNhfstYWB1aWSNBc");
    params.add("code", code);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
      Map<String, Object> body = response.getBody();

      if (body != null && body.containsKey("access_token")) {
        String accessToken = (String) body.get("access_token");
        return accessToken;
      } else {
        throw new RuntimeException("카카오 access token 발급 실패 - 응답에 access_token 없음");
      }

    } catch (HttpClientErrorException e) {
      throw new RuntimeException("카카오 access token 발급 실패", e);
    } catch (Exception e) {
      throw new RuntimeException("카카오 access token 요청 중 오류", e);
    }
  }

  private OAuthUserInfo requestUserInfo(String accessToken) {
    // RestTemplate 사용, 카카오 API에 GET 요청
    // JSON 응답 → OAuthUserInfo로 변환
    String url = "https://kapi.kakao.com/v2/user/me";

    // HTTP 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken); // Authorization: Bearer {accessToken}

    HttpEntity<Void> request = new HttpEntity<>(headers);

    // GET 요청 보내기
    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    Map<String, Object> body = response.getBody();

    if (body == null) {
      throw new RuntimeException("카카오 사용자 정보 요청 실패");
    }

    // kakao_account 안에서 email, profile 정보 가져오기
    Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
    String email = (String) kakaoAccount.get("email");

    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
    String providerUserId = String.valueOf(body.get("id"));
    String nickname = (String) profile.get("nickname");
    String profileImageUrl = (String) profile.get("profile_image_url");

    return new OAuthUserInfo(providerUserId, email, nickname, profileImageUrl);
  }
}
