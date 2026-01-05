package com.windfall.api.searchHistory.dto.response;

import com.windfall.api.searchHistory.dto.response.info.SearchHistoryReadInfo;
import java.util.List;

public record SearchHistoryReadResponse(
    Long userId,
    List<SearchHistoryReadInfo> searchHistoryReadInfos
) {
  public static SearchHistoryReadResponse of(Long userId, List<SearchHistoryReadInfo> searchHistoryReadInfos) {
    return new SearchHistoryReadResponse(userId, searchHistoryReadInfos);
  }
}
