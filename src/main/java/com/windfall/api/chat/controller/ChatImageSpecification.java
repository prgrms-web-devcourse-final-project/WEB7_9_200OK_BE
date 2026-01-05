package com.windfall.api.chat.controller;

import static com.windfall.global.exception.ErrorCode.EMPTY_CHAT_IMAGE;
import static com.windfall.global.exception.ErrorCode.INVALID_CHAT_IMAGE_COUNT;
import static com.windfall.global.exception.ErrorCode.INVALID_DIRECTORY_NAME;
import static com.windfall.global.exception.ErrorCode.INVALID_S3_UPLOAD;
import static com.windfall.global.exception.ErrorCode.INVALID_UPLOAD_FILE;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_USER;

import com.windfall.api.chat.dto.response.ChatImageUploadResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.config.swagger.ApiErrorCodes;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Chat Image", description = "채팅 이미지 업로드 API")
public interface ChatImageSpecification {

  @ApiErrorCodes({NOT_FOUND_USER, EMPTY_CHAT_IMAGE, INVALID_CHAT_IMAGE_COUNT, INVALID_UPLOAD_FILE,
      INVALID_S3_UPLOAD, INVALID_DIRECTORY_NAME})
  @Operation(
      summary = "채팅 이미지 업로드",
      description = """
          채팅용 이미지를 S3에 업로드하고 URL 목록을 반환합니다.
          반환된 URL로 WebSocket IMAGE 메시지를 전송하세요.
          """
  )
  ApiResponse<List<ChatImageUploadResponse>> uploadChatImages(
      @RequestPart(name = "uploadFiles") List<MultipartFile> files,
      @AuthenticationPrincipal CustomUserDetails userDetails
  );
}
