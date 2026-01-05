package com.windfall.api.searchHistory.service;

import com.windfall.api.searchHistory.dto.response.SearchHistoryReadResponse;
import com.windfall.domain.searchHistory.entity.SearchHistory;
import com.windfall.domain.searchHistory.repository.SearchHistoryRepository;
import com.windfall.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {
  private final SearchHistoryRepository searchHistoryRepository;


  @Async
  @Transactional
  public void createSearchHistory(User user, String keyword) {
    searchHistoryRepository.save(
        SearchHistory.create(user, keyword)
    );
  }

  public SearchHistoryReadResponse readSearchHistory(Long userId) {
    return searchHistoryRepository.findRecentKeywords(userId, 15);
  }

  public void deleteByUserId(Long searchId){
    searchHistoryRepository.deleteById(searchId);
  }
}
