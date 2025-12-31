package com.windfall.global.websocket;

import java.security.Principal;

public class StompPrincipal implements Principal {
  private final String name;

  public StompPrincipal(Long userId) {
    this.name = String.valueOf(userId);
  }
  @Override public String getName() {
    return name;
  }
}