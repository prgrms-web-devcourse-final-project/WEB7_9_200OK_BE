package com.windfall.api.chat.controller;

import com.windfall.api.chat.dto.request.enums.ChatRoomScope;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Chat", description = "채팅방 API")
public interface ChatRoomSpecification {

  @Operation(
      summary = "채팅방 목록 조회",
      description = """
          로그인한 사용자의 채팅방 목록을 조회합니다.
          - scope=ALL: 전체 대화
          - scope=BUY: 구매자로 참여한 대화
          - scope=SELL: 판매자로 참여한 대화
          정렬: 마지막 메시지 시간(lastMessageAt) 최신순 (없으면 채팅방 생성일 기준)
          unreadCount: 상대방이 보낸 메시지 중 읽지 않은 메시지 개수
          """
  )
  ApiResponse<?> getChatRooms(
      @Parameter(description = "필터 범위", required = true, example = "ALL")
      @RequestParam ChatRoomScope scope,

      @Parameter(description = "페이지(0부터)", example = "0")
      @RequestParam(defaultValue = "0") int page,

      @Parameter(description = "사이즈", example = "20")
      @RequestParam(defaultValue = "20") int size,

      @Parameter(description = "사용자 ID(임시, 로그인 붙이면 제거 예정)", example = "1")
      @RequestParam(defaultValue = "1") Long userId
  );

}
