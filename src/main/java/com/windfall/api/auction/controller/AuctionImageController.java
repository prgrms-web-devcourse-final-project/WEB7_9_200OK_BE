package com.windfall.api.auction.controller;

import com.windfall.api.auction.dto.response.ImageUploadResponse;
import com.windfall.api.auction.service.AuctionImageService;
import com.windfall.global.response.ApiResponse;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auction-images")
@RequiredArgsConstructor
public class AuctionImageController implements AuctionImageSpecification{

  private final AuctionImageService auctionImageService;


  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<List<ImageUploadResponse>> upload (
      @Size(max = 10,message = "이미지 등록 개수를 초과했습니다.") @RequestPart(name = "uploadFiles", required = false) List<MultipartFile> files
  ) {
    List<ImageUploadResponse> imageIds = auctionImageService.upload(files);

    return ApiResponse.created("이미지가 업로드 되었습니다.", imageIds);
  }
}
