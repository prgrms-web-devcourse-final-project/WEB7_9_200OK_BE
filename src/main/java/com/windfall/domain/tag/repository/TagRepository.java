package com.windfall.domain.tag.repository;

import com.windfall.domain.tag.entity.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {

  Optional<Tag> findByTagName(String tagName);

  @Query("""
          SELECT t
          FROM Tag t
          WHERE t.tagName LIKE CONCAT(:keyword, '%')
          ORDER BY t.tagName ASC
      """)
  List<Tag> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}