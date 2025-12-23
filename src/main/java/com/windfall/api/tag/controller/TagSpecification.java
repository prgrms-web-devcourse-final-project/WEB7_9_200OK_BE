package com.windfall.api.tag.controller;

import com.windfall.api.tag.dto.response.SearchTagResponse;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Tag", description = "태그 API")
public interface TagSpecification {

  @Operation(summary = "태그 자동 완성", description = "태그 검색 시 `자동 완성`을 5개 제공합니다.")
  ApiResponse<SearchTagResponse> searchTag(
      @Parameter(description = "태그 검색어", example = "뉴발")
      @RequestParam String keyword
  );
}