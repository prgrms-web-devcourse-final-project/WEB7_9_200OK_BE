package com.windfall.api.user.controller;


import com.windfall.domain.user.entity.CustomUserDetails;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PrincipalController {

  @GetMapping("/auth/test")
  public ResponseEntity<?> principalTest(
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    return ResponseEntity.ok(
        Map.of(
            "userId", user.getUserId(),
            "provider", user.getProvider(),
            "providerUserId", user.getProviderUserId(),
            "email", user.getEmail(),
            "nickname", user.getNickname()
        )
    );
  }
}
