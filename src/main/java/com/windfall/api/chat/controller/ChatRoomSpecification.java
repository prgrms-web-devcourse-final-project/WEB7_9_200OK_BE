package com.windfall.api.chat.controller;

import com.windfall.api.chat.dto.request.enums.ChatRoomScope;
import com.windfall.api.chat.dto.response.ChatRoomDetailResponse;
import com.windfall.api.chat.dto.response.ChatRoomListResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
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

      @Parameter(description = "사용자 ID", required = true, example = "1")
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

  @Operation(
      summary = "채팅방 상세 조회 (메타 정보 + 메시지 내용 커서 페이징)",
      description = """
          채팅방 상세 화면에 필요한 정보를 조회합니다.
          - 상단 메타(상대방/경매/거래 정보) + 메시지 목록(cursor 기반)을 한 번에 반환합니다.
          - cursor가 없으면 최신 메시지부터 size 만큼 반환합니다.
          - nextCursor를 다음 요청에 전달하면 더 과거 메시지를 조회합니다.
          """
  )
  ApiResponse<ChatRoomDetailResponse> getChatRoomDetail(
      @Parameter(description = "채팅방 ID", example = "1")
      @PathVariable Long chatRoomId,

      @Parameter(description = "커서(이전 페이지의 nextCursor)", example = "120")
      @RequestParam(required = false) Long cursor,

      @Parameter(description = "조회 개수", example = "20")
      @RequestParam(defaultValue = "20") int size,

      @Parameter(description = "사용자 ID", required = true, example = "1")
      @AuthenticationPrincipal CustomUserDetails userDetails
  );

}
