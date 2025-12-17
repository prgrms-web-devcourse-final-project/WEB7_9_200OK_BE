package com.windfall.api.user.service;

import com.windfall.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
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
  public String generateAccessToken(User user) {
    return Jwts.builder()
        .setSubject(user.getProviderUserId())
        .claim(user.getProviderUserId(), "")
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity * 1000))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // Refresh Token 생성
  public String generateRefreshToken(User user) {
    return Jwts.builder()
        .setSubject(user.getProviderUserId())
        .claim(user.getProviderUserId(), "")
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
        System.out.println("토큰 검사했는데 만료 정보 없음, 비정상");
        return false;
      }

      boolean notExpired = expiration.after(new Date());
      System.out.println("토큰 검사했는데 " + (notExpired ? "정상입니다." : "만료되었습니다."));
      return notExpired;

    } catch (JwtException | IllegalArgumentException e) {
      System.out.println("토큰 검사했는데 비정상입니다.");
      return false;
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
}