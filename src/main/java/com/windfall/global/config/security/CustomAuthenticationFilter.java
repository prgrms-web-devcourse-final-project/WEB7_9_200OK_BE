package com.windfall.global.config.security;

import com.windfall.api.user.service.JwtProvider;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.domain.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

  private final UserService userService;
  private final JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // preflight 통과
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = resolveToken(request);
    log.info("[PAYMENT] token = {}", token);
    log.info("[PAYMENT] uri = {}", request.getRequestURI());
    if (token == null || token.isBlank()) {
      filterChain.doFilter(request, response);
      return;
    }



    String providerUserId = jwtProvider.getProviderUserId(token);
    User user = userService.getUserByProviderUserId(providerUserId);

    CustomUserDetails userDetails = new CustomUserDetails(user);

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

    authentication.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request)
    );


    SecurityContextHolder.getContext().setAuthentication(authentication);

    Authentication authInfo = SecurityContextHolder.getContext().getAuthentication();

    if (authInfo != null && authentication.getPrincipal() instanceof CustomUserDetails userInfo) {
      log.info("로그인 사용자 ID: {}", userInfo.getUserId());
      log.info("로그인 사용자 이름: {}", userInfo.getUsername());
      log.info("권한: {}", userInfo.getAuthorities());
    } else {
      log.info("SecurityContext에 인증 정보가 없음 또는 Principal이 CustomUserDetails가 아님");
    }


    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }

    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;

    for (Cookie c : cookies) {
      if ("accessToken".equals(c.getName())) {
        return c.getValue();
      }
    }
    return null;
  }
}