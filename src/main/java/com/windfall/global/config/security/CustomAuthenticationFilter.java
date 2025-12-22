package com.windfall.global.config.security;

import com.windfall.api.user.service.JwtProvider;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.user.entity.User;
import com.windfall.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // preflight 통과
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = resolveToken(request);

    if (token == null || token.isBlank()) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!jwtProvider.validateToken(token)) {
      response.setContentType("application/json;charset=UTF-8");
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write("""
        {
          "resultCode": "%s",
          "msg": "%s"
        }
        """.formatted(
          ErrorCode.INVALID_TOKEN.name(),
          ErrorCode.INVALID_TOKEN.getMessage()
      ));
      return;
    }

    String providerUserId = jwtProvider.getProviderUserId(token);
    User user = userService.getUserByProviderUserId(providerUserId);

    UserDetails userDetails = org.springframework.security.core.userdetails.User
        .withUsername(user.getProviderUserId())
        .password("")
        .authorities("ROLE_USER")
        .build();

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
