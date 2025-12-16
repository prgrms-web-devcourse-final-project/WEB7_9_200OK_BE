package com.windfall.domain.tag.repository;

import com.windfall.api.tag.document.TagDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TagSearchRepository
    extends ElasticsearchRepository<TagDocument, String> {
}