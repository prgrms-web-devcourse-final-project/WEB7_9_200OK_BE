package com.windfall.api.user.dto.response;

public record RegisterUserResponse(
    String userEmail,
    String userNickname,
    String userProfileUrl,
    String accessToken,
    String refreshToken
) {}
