package com.windfall.api.auction.service;


import com.windfall.api.auction.dto.response.ImageUploadResponse;
import com.windfall.domain.auction.entity.AuctionImage;
import com.windfall.domain.auction.enums.ImageStatus;
import com.windfall.domain.auction.repository.AuctionImageRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.io.InputStream;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class AuctionImageService {

  private final AuctionImageRepository auctionImageRepository;
  private final S3Client s3Client;

  @Value("${aws.s3.bucketName}")
  private String bucketName;

  @Value("${aws.s3.baseUrl}")
  private  String s3BaseUrl;

  @Transactional
  public List<ImageUploadResponse> upload(List<MultipartFile> files) {
    return files.stream()
        .map(this::uploadSingleFileAndSave)
        .collect(Collectors.toList());
  }

  private ImageUploadResponse uploadSingleFileAndSave(MultipartFile file){
    String objectKey = s3BaseUrl  + uploadToS3(file);

    AuctionImage auctionImage = AuctionImage.create(objectKey, file.getSize(), ImageStatus.TEMP);
    AuctionImage saved = auctionImageRepository.save(auctionImage);

    return ImageUploadResponse.from(saved);
  }

  private String uploadToS3(MultipartFile file) {
    String objectKey = generateObjectKey();

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(objectKey)
        .contentType(file.getContentType())
        .contentLength(file.getSize())
        .build();


    try (InputStream inputStream = file.getInputStream()) {
      s3Client.putObject(putObjectRequest,
          RequestBody.fromInputStream(inputStream, file.getSize()));
    } catch (IOException e) {
      throw new ErrorException(ErrorCode.INVALID_S3_UPLOAD);
    }

    return objectKey;
  }

  private String generateObjectKey() {
    int path = LocalDateTime.now().getNano();
    return  path + "/" + UUID.randomUUID();
  }
}
