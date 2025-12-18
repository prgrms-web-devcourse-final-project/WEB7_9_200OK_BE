package com.windfall.api.user.dto.response;

// 로그인 응답 DTO
public record LoginUserResponse(
    String userEmail,
    String username,
    String userProfileUrl
) {}