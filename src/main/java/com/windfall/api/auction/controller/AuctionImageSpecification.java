package com.windfall.api.auction.controller;

import static com.windfall.global.exception.ErrorCode.INVALID_S3_UPLOAD;
import com.windfall.api.auction.dto.response.ImageUploadResponse;
import com.windfall.global.config.swagger.ApiErrorCodes;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Auction Image", description = "경매 이미지 관련 API")
public interface AuctionImageSpecification {

  @ApiErrorCodes({INVALID_S3_UPLOAD})
  @Operation(summary = "이미지 업로드", description = "이미지를 업로드하는 API입니다.")
  ApiResponse<List<ImageUploadResponse>> upload (
      @Size(max = 10,message = "이미지 등록 개수를 초과했습니다.") @RequestPart(name = "uploadFiles", required = false) List<MultipartFile> files
  );
}
