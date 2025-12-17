package com.windfall.api.user.service;

import com.windfall.domain.user.entity.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtProvider {
  private final String secretKey;
  private final long accessTokenValidity;
  private final long refreshTokenValidity;

  public JwtProvider(
      @Value("${custom.jwt.secretPattern}") String secretKey,
      @Value("${custom.jwt.expireSecondsAccessToken}") long accessTokenValidity,
      @Value("${custom.jwt.expireSecondsRefreshToken}") long refreshTokenValidity
  ) {
    this.secretKey = secretKey;
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
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  // Refresh Token 생성
  public String generateRefreshToken(User user) {
    return Jwts.builder()
        .setSubject(user.getProviderUserId())
        .claim(user.getProviderUserId(), "")
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity * 1000))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  // JWT 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  // JWT에서 사용자 ID 추출
  public String getUserId(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }
}