package com.windfall.api.chat.service;

import com.windfall.api.chat.dto.response.ChatImageUploadResponse;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import com.windfall.global.s3.S3Uploader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChatImageService {

  private static final int MAX_CHAT_IMAGE_COUNT = 5;

  private final S3Uploader s3Uploader;

  @Transactional(readOnly = true)
  public List<ChatImageUploadResponse> upload(List<MultipartFile> files, Long userId) {
    validateChatImages(files);

    String dirName = "chat/images/" + userId;

    List<String> urls = s3Uploader.upload(files, dirName);

    return urls.stream()
        .map(ChatImageUploadResponse::of)
        .toList();
  }

  private void validateChatImages(List<MultipartFile> files) {
    if (files == null || files.isEmpty()) {
      throw new ErrorException(ErrorCode.EMPTY_CHAT_IMAGE);
    }
    if (files.size() > MAX_CHAT_IMAGE_COUNT) {
      throw new ErrorException(ErrorCode.INVALID_CHAT_IMAGE_COUNT);
    }
  }

}
