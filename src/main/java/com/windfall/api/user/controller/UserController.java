//
// 구체적으로 필요한 함수는 다음 PR 때 적겠습니다.
package com.windfall.api.user.controller;

import com.windfall.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController implements UserSpecification {

  private final UserService userService;
}
