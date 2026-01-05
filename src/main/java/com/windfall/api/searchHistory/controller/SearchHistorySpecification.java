package com.windfall.api.searchHistory.controller;

import com.windfall.api.searchHistory.dto.response.SearchHistoryReadResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface SearchHistorySpecification {
  ApiResponse<SearchHistoryReadResponse> readSearchHistory(
      @AuthenticationPrincipal CustomUserDetails user
  );
}
