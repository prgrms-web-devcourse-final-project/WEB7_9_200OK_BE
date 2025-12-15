// RestTemplateConfig는 RestTemplate을 의존성 주입 가능한 빈으로 만듭니다.
// RestTemplate 타입 객체는 OAuthKakaoService, OAuthNaverService, OAuthGoogleService에서 사용됩니다.

package com.windfall.api.user.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}