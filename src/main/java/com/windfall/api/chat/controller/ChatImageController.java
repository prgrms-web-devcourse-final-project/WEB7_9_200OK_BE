package com.windfall.api.chat.controller;

import com.windfall.api.chat.dto.response.ChatImageUploadResponse;
import com.windfall.api.chat.service.ChatImageService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-images")
public class ChatImageController implements ChatImageSpecification{

  private final ChatImageService chatImageService;

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<List<ChatImageUploadResponse>> uploadChatImages(
      @RequestPart(name = "uploadFiles") List<MultipartFile> files,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getUserId();
    List<ChatImageUploadResponse> response = chatImageService.upload(files, userId);
    return ApiResponse.ok("채팅 이미지가 업로드 되었습니다.", response);
  }
}
