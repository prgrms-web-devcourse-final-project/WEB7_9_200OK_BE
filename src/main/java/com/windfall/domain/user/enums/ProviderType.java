package com.windfall.domain.user.enums;

import lombok.Getter;

@Getter
public enum ProviderType {
  GOOGLE("google"),
  KAKAO("kakao"),
  NAVER("naver");

  private final String providerName;

  ProviderType(String providerName) {
    this.providerName = providerName;
  }
}
