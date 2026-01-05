package com.windfall.domain.searchHistory.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.windfall.api.searchHistory.dto.response.SearchHistoryReadResponse;
import com.windfall.api.searchHistory.dto.response.info.SearchHistoryReadInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;


import static com.windfall.domain.searchHistory.entity.QSearchHistory.searchHistory;

@RequiredArgsConstructor
public class SearchHistoryRepositoryCustomImpl implements SearchHistoryRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public SearchHistoryReadResponse findRecentKeywords(Long userId, int limit) {

    List<SearchHistoryReadInfo> searchHistoryReadInfos = queryFactory
        .select(Projections.constructor(
            SearchHistoryReadInfo.class,
            searchHistory.id.max(),
            searchHistory.keyword
        ))
        .from(searchHistory)
        .where(searchHistory.user.id.eq(userId))
        .groupBy(searchHistory.keyword)
        .orderBy(searchHistory.createDate.max().desc())
        .limit(limit)
        .fetch();

    return SearchHistoryReadResponse.of(userId, searchHistoryReadInfos);
  }
}
