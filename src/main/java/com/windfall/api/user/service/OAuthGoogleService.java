// OAuthKakaoService, OAuthNaverService, OAuthGoogleService가 겹치는 것은 추후 리팩토링 때 정리하겠습니다.
// 각 provider 회사마다 나눈 이유는, 각 회사가 전달하는 데이터 로직이 (미래를 고려해) 미묘하게 달라질 수 있다고 하기 때문입니다.
// 연관 파일
//   RestTemplateConfig.java

package com.windfall.api.user.service;

import com.windfall.api.user.dto.response.OAuthTokenResponse;
import com.windfall.api.user.dto.response.OAuthUserInfo;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.entity.UserToken;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.domain.user.repository.UserRepository;
import com.windfall.domain.user.repository.UserTokenRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuthGoogleService {

  private final UserRepository userRepository;
  private final UserTokenRepository userTokenRepository;
  private final JwtProvider jwtProvider; // JWT 발급용
  private final RestTemplate restTemplate;

  @Value("${spring.google.client.id}")
  private String googleClientId;

  @Value("${spring.google.client.secret}")
  private String googleClientSecret;

  @Value("${spring.google.redirect.uri}")
  private String googleRedirectUri;

  @Transactional
  public OAuthTokenResponse loginOrSignup(OAuthUserInfo userInfo) {

    User user = userRepository.findByProviderUserId(userInfo.providerUserId())
        .orElseGet(() -> userRepository.save(
            new User(ProviderType.GOOGLE, userInfo.providerUserId(), userInfo.email(),
                userInfo.nickname(), userInfo.profileImageUrl())
        ));

    String jwtAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getProviderUserId());
    String jwtRefreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getProviderUserId());

    userTokenRepository.findByUser(user)
        .ifPresentOrElse(
            userToken -> userToken.saveRefreshToken(jwtRefreshToken),
            () -> userTokenRepository.save(UserToken.create(user, jwtRefreshToken))
        );

    return new OAuthTokenResponse(
        user.getId(),
        jwtAccessToken,
        jwtRefreshToken
    );
  }

  public String requestAccessToken(String code) {
    String url = "https://oauth2.googleapis.com/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", googleClientId);
    params.add("client_secret", googleClientSecret);
    params.add("code", code);
    params.add("redirect_uri", googleRedirectUri);

    System.out.println("client_id : " + googleClientId);
    System.out.println("code : " + code);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
      Map<String, Object> body = response.getBody();

      if (body != null && body.containsKey("access_token")) {
        String accessToken = (String) body.get("access_token");
        return accessToken;
      } else {
        throw new RuntimeException("구글 access token 발급 실패 - 응답에 access_token 없음");
      }

    } catch (HttpClientErrorException e) {
      throw new RuntimeException("구글 access token 발급 실패", e);
    } catch (Exception e) {
      throw new RuntimeException("구글 access token 요청 중 오류", e);
    }
  }

  public OAuthUserInfo requestUserInfo(String accessToken) {
    // RestTemplate 사용, 구글 API에 GET 요청
    // JSON 응답 → OAuthUserInfo로 변환
    String url = "https://www.googleapis.com/oauth2/v2/userinfo";

    // HTTP 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken); // Authorization: Bearer {accessToken}

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<Map> response =
        restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

    Map<String, Object> body = response.getBody();
    if (body == null) {
      throw new RuntimeException("구글 사용자 정보 응답 바디 없음");
    }

    String providerUserId = (String) body.get("id");
    String email = (String) body.get("email");
    String nickname = (String) body.get("name");
    String profileImageUrl = (String) body.get("picture");

    return new OAuthUserInfo(providerUserId, email, nickname, profileImageUrl);
  }
}
