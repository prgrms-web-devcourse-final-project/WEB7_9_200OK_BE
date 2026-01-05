package com.windfall.api.searchHistory.controller;


import com.windfall.api.searchHistory.dto.response.SearchHistoryReadResponse;
import com.windfall.api.searchHistory.service.SearchHistoryService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/searches")
@RequiredArgsConstructor
public class SearchHistoryController implements SearchHistorySpecification {

  private final SearchHistoryService searchHistoryService;

  @Override
  @GetMapping
  public ApiResponse<SearchHistoryReadResponse> readSearchHistory(
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    SearchHistoryReadResponse response = searchHistoryService.readSearchHistory(1L);
    return ApiResponse.ok("경매 목록 조회에 성공했습니다.", response);
  }

  @DeleteMapping("/{searchId}")
  public ApiResponse<Void> deleteSearchHistory(
      @PathVariable Long searchId
  )
  {
    searchHistoryService.deleteByUserId(searchId);
    return ApiResponse.noContent();
  }
}
