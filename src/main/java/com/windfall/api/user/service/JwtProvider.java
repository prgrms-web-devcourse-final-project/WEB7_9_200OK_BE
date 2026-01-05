package com.windfall.api.user.service;

import com.windfall.domain.user.enums.JwtValidationResult;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtProvider {

  private final Key key; // SecretKey로 변환
  private final long accessTokenValidity;
  private final long refreshTokenValidity;

  public JwtProvider(
      @Value("${custom.jwt.secretPattern}") String secretKey,
      @Value("${custom.jwt.expireSecondsAccessToken}") long accessTokenValidity,
      @Value("${custom.jwt.expireSecondsRefreshToken}") long refreshTokenValidity
  ) {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    this.accessTokenValidity = accessTokenValidity;
    this.refreshTokenValidity = refreshTokenValidity;
  }

  // Access Token 생성
  public String generateAccessToken(Long id, String providerUserId) {
    return Jwts.builder()
        .setSubject(providerUserId)
        .claim(providerUserId, "")
        .claim("userId", id)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity * 1000))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // Refresh Token 생성
  public String generateRefreshToken(Long id, String providerUserId) {
    return Jwts.builder()
        .setSubject(providerUserId)
        .claim(providerUserId, "")
        .claim("userId", id)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity * 1000))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // JWT 검증
  public boolean validateToken(String token) {
    try {
      // JWT 파싱 (서명 검증 포함)
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();

      // 만료일(exp) 확인
      Date expiration = claims.getExpiration();
      if (expiration == null) {
        // exp가 없으면 만료로 간주
        return false;
      }

      boolean notExpired = expiration.after(new Date());
      return notExpired;

    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public JwtValidationResult validateTokenWithResult(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();

      Date expiration = claims.getExpiration();
      if (expiration == null) return JwtValidationResult.INVALID; // exp 없으면 비정상 토큰
      // parseClaimsJws 단계에서 만료면 ExpiredJwtException이 터지므로 여기까지 오면 유효
      return JwtValidationResult.VALID;

    } catch (ExpiredJwtException e) {
      return JwtValidationResult.EXPIRED;
    } catch (JwtException | IllegalArgumentException e) {
      return JwtValidationResult.INVALID;
    }
  }

  // JWT에서 사용자 ID 추출
  public String getProviderUserId(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      throw new ErrorException(ErrorCode.EMPTY_REFRESH_TOKEN);
    }

    return Arrays.stream(request.getCookies())
        .filter(c -> "refreshToken".equals(c.getName()))
        .findFirst()
        .map(Cookie::getValue)
        .orElseThrow(() -> new ErrorException(ErrorCode.EMPTY_REFRESH_TOKEN));
  }

  public Cookie generateCookieWithAccessToken(String token) {
    Cookie accessTokenCookie = new Cookie("accessToken", token);
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setSecure(true);
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(1 * 1 * 60 * 60); // 1시간
    return accessTokenCookie;
  }

  public Cookie generateCookieWithRefreshToken(String token) {
    Cookie refreshTokenCookie = new Cookie("refreshToken", token);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 (일주일)
    return refreshTokenCookie;
  }

  public Long extractUserId(String token) {
    // refreshToken이 만료되면 내부 값이 추출할 수 없다.
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();

      return claims.get("userId", Long.class);

    } catch (ExpiredJwtException e) {
      throw new ErrorException(ErrorCode.EXPIRED_REFRESH_TOKEN);

    } catch (JwtException | IllegalArgumentException e) {
      throw new ErrorException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
  }
  
  public Long getUserId(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

    Object v = claims.get("userId");
    if (v == null) return null;
    if (v instanceof Integer i) return i.longValue();
    if (v instanceof Long l) return l;
    return Long.valueOf(String.valueOf(v));
  }
}