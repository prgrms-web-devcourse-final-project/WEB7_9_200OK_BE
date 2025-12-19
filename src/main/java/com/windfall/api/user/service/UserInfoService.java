package com.windfall.api.user.service;

import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

  private final UserService userService;
  private final UserRepository userRepository;

  @Transactional
  public UserInfoResponse getUserInfo(Long userid, Long loginId){ //userDetails 추가될 경우 리팩토링 예정
    userService.getUserById(userid); //조회할 유저가 있는지 먼저 검색

    return userRepository.findByUserInfo(userid, loginId);
  }

}
