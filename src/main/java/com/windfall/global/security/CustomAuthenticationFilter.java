package com.windfall.global.security;

import com.windfall.api.user.service.JwtProvider;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.user.entity.User;
import com.windfall.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

  private final UserService userService;
  private final JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    logger.debug("CustomAuthenticationFilter called");

    try {
      authenticate(request, response, filterChain);
    } catch (Exception e) {
      // JWT 검증 등 일반 예외 처리
      response.setContentType("application/json");
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write("""
        {
            "resultCode": "%s",
            "msg": "%s"
        }
        """.formatted(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage()));
    }
  }

  private void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();

    // 1. 예외 URL 패스
    if (path.startsWith("/api/v1") ||
        path.startsWith("/swagger-ui") ||
        path.startsWith("/v3/api-docs") ||
        path.startsWith("/swagger-resources") ||
        path.startsWith("/webjars")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Authorization 헤더 확인
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    String token = authHeader.substring(7); // "Bearer " 제거
    // 3. accessToken 검증
    if (!jwtProvider.validateToken(token)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    // 4. 사용자 정보 가져오기
    String providerUserId = jwtProvider.getUserId(token);
    User user = userService.getUserByProviderUserId(providerUserId);
    UserDetails userDetails = org.springframework.security.core.userdetails.User
        .withUsername(user.getProviderUserId())
        .password("") // OAuth라 빈 문자열 넣기.
        .authorities("ROLE_USER")    // 권한 설정
        .build();

    // 5. SecurityContext 세팅
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 6. 다음 필터로 진행
    filterChain.doFilter(request, response);
  }
}