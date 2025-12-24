package com.windfall.api.user.controller;

import com.windfall.domain.user.entity.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface PrincipalSpecification {

  @Operation(summary = "AuthenticationPrincipal로 유저 정보 잘 불러와지나 체크", description = "체크합니다.")
  ResponseEntity<?> principalTest(@AuthenticationPrincipal CustomUserDetails user);
}
