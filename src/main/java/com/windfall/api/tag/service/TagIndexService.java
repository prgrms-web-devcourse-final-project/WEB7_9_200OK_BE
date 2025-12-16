package com.windfall.api.tag.service;

import com.windfall.api.tag.document.TagDocument;
import com.windfall.domain.tag.repository.TagSearchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagIndexService {

  private final TagSearchRepository repository;

  @Transactional
  public void indexTags(List<String> tags) {
    for (String tag : tags) {
      indexSingleTag(normalize(tag));
    }
  }

  private String normalize(String tag) {
    return tag.toLowerCase();
  }

  private void indexSingleTag(String normalizedTag) {
    repository.findById(normalizedTag)
        .ifPresentOrElse(
            doc -> {
              doc.setUseCount(doc.getUseCount() + 1);
              repository.save(doc);
            },
            () -> repository.save(
                TagDocument.builder()
                    .tag(normalizedTag)
                    .useCount(1L)
                    .build()
            )
        );
  }
}