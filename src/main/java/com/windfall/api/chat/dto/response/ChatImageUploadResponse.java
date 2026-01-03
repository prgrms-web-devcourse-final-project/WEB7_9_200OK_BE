package com.windfall.api.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 이미지 업로드 응답 DTO")
public record ChatImageUploadResponse(

    @Schema(description = "업로드된 이미지 URL")
    String imageUrl

) {
  public static ChatImageUploadResponse of(String url) {
    return new ChatImageUploadResponse(url);
  }
}
