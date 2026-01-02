package com.windfall.api.mypage.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record DashBoardCalenderDTO (
    @Schema(description = "날짜 (yyyy-MM)")
    LocalDate date,

    @Schema(description = "경매 예정 수")
    int scheduled,

    @Schema(description = "경매 진행중 수")
    int process
)
{

}
