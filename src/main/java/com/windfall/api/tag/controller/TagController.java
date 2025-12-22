package com.windfall.api.tag.controller;

import com.windfall.api.tag.dto.response.SearchTagResponse;
import com.windfall.api.tag.service.TagService;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController implements TagSpecification{

  private final TagService tagService;

  @Override
  @GetMapping("/search")
  public ApiResponse<SearchTagResponse> searchTag(
      @RequestParam String keyword
  ) {
    SearchTagResponse response = tagService.searchTag(keyword);
    return ApiResponse.ok("태그 검색에 성공했습니다.", response);
  }
}