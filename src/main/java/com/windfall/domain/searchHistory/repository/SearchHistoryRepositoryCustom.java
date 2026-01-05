package com.windfall.domain.searchHistory.repository;


import com.windfall.api.searchHistory.dto.response.SearchHistoryReadResponse;


public interface SearchHistoryRepositoryCustom  {
  SearchHistoryReadResponse findRecentKeywords(Long userId, int limit);
}
