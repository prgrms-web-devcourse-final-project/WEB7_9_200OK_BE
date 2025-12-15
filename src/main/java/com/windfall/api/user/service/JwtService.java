package com.windfall.api.user.service;

import com.windfall.domain.user.entity.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final String secretKey = "JWT_SECRET_KEY"; // application.yml에서 관리 가능
  private final long accessTokenValidity = 3600_000; // 1시간
  private final long refreshTokenValidity = 604_800_000; // 7일

  // Access Token 생성
  public String generateAccessToken(User member) {
    return Jwts.builder()
        .setSubject(member.getProviderUserId())
        .claim("username", "")
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  // Refresh Token 생성
  public String generateRefreshToken(User member) {
    return Jwts.builder()
        .setSubject(member.getProviderUserId())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
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