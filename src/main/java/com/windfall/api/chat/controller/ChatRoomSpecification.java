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
          정렬: lastMessageAt 내림차순(최신순)
          unreadCount: 상대방이 보낸 메시지 중 읽지 않은 메시지 개수
          """
  )
  ApiResponse<List<ChatRoomListResponse>> getChatRooms(
      @Parameter(description = "필터 범위 (ALL/BUY/SELL)", example = "ALL")
      @RequestParam(defaultValue = "ALL") ChatRoomScope scope,

      @Parameter(description = "사용자 ID(임시, 로그인 붙이면 제거 예정)", example = "1")
      @RequestParam(defaultValue = "1") Long userId
  );

}
