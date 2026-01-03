package com.windfall.global.s3;

import com.windfall.global.config.s3.S3Properties;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3Uploader {

  private final S3Client s3Client;
  private final S3Properties s3Properties;

  // 단일 업로드
  public String upload(MultipartFile file, String dirName) {
    if (file == null || file.isEmpty()) {
      throw new ErrorException(ErrorCode.INVALID_UPLOAD_FILE);
    }

    String key = createKey(dirName, file.getOriginalFilename());

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(s3Properties.getBucketName())
        .key(key)
        .contentType(file.getContentType())
        .contentLength(file.getSize())
        .build();

    try (InputStream is = file.getInputStream()) {
      s3Client.putObject(request, RequestBody.fromInputStream(is, file.getSize()));
      return buildUrl(key);
    } catch (Exception e) {
      throw new ErrorException(ErrorCode.INVALID_S3_UPLOAD);
    }
  }

  // 다중 업로드
  public List<String> upload(List<MultipartFile> files, String dirName) {
    if (files == null || files.isEmpty()) {
      throw new ErrorException(ErrorCode.INVALID_UPLOAD_FILE);
    }

    return files.stream()
        .map(f -> upload(f, dirName))
        .toList();
  }

  private String createKey(String dirName, String originalFilename) {
    String dir = normalizeDir(dirName);
    String uuid = UUID.randomUUID().toString();

    String name =
        (originalFilename == null || originalFilename.isBlank()) ? "file" : originalFilename;

    return dir + "/" + uuid + "-" + name;
  }

  private String normalizeDir(String dirName) {
    if (dirName == null || dirName.isBlank()) {
      throw new ErrorException(ErrorCode.INVALID_DIRECTORY_NAME);
    }
    String dir = dirName;
    while (dir.startsWith("/")) {
      dir = dir.substring(1);
    }
    while (dir.endsWith("/")) {
      dir = dir.substring(0, dir.length() - 1);
    }
    return dir;
  }

  private String buildUrl(String key) {
    String baseUrl = s3Properties.getBaseUrl();
    if (baseUrl.endsWith("/")) {
      return baseUrl + key;
    }
    return baseUrl + "/" + key;
  }
}