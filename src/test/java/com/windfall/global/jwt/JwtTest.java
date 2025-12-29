package com.windfall.global.jwt;

import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class JwtTest{

  @Autowired
  protected UserRepository userRepository;


  protected User mockUser;

  @BeforeEach
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  void setUp() {

    User user = User.builder()
        .email("test@naver.com")
        .provider(ProviderType.NAVER)
        .providerUserId("test1234")
        .nickname("testNickname")
        .build();

    mockUser = userRepository.save(user);

    CustomUserDetails userDetails = new CustomUserDetails(mockUser);

    Authentication auth = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
