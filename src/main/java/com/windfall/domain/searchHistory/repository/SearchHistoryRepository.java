package com.windfall.domain.searchHistory.repository;

import com.windfall.domain.searchHistory.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory,Long>, SearchHistoryRepositoryCustom {

}
