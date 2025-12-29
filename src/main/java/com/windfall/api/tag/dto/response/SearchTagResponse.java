package com.windfall.api.tag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "태그 검색 응답 DTO")
public record SearchTagResponse (

    @Schema(description = "태그 자동 완성")
    List<SearchTagInfo> tags
){

  public static SearchTagResponse from(List<SearchTagInfo> tags) {
    return new SearchTagResponse(tags);
  }

  public static SearchTagResponse empty() {
      return new SearchTagResponse(List.of());
  }
}