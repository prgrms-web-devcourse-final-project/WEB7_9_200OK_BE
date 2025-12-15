//
// UserSpecification 인터페이스는 UserController 클래스의 Swagger 설명문을 담당합니다.

package com.windfall.api.user.controller;

import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User", description = "사용자 API")
public interface UserSpecification {

  @Operation(summary = "로그인 URL 반환", description = "provier에 맞는 로그인 url을 반환합니다.")
  ApiResponse<String> redirectToLogin(@RequestParam String provider);
}
