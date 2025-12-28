package com.windfall.global.response;

import java.time.LocalDateTime;
import java.util.List;

public record CursorResponse<T>(
    List<T> content,
    Long nextCursor,
    boolean hasNext,
    int size,
    LocalDateTime timeStamp


) {
  public static <T> CursorResponse<T> of(List<T> content, Long nextCursor, boolean hasNext, int size) {
    return new CursorResponse<>(content, nextCursor, hasNext, size, LocalDateTime.now());
  }
}
