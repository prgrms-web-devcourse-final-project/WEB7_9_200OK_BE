package com.windfall.global.response;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Slice;

public record SliceResponse<T>(
    List<T> slice,
    boolean hasNext, // 다음 페이지 존재 여부
    int page,       // 현재 페이지
    int size,       // 요청 사이즈
    LocalDateTime timeStamp   // 서버 시간
) {
  public static <T> SliceResponse<T> from(Slice<T> slice) {
    return new SliceResponse<T>(
        slice.getContent(),
        slice.hasNext(),
        slice.getNumber(),
        slice.getSize(),
        LocalDateTime.now()
    );
  }
}
