package com.windfall.api.mypage.service;

import com.windfall.api.mypage.dto.dashboard.DashBoardCalenderDTO;
import com.windfall.domain.mypage.repository.DashBoardRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashBoardService {

  private final DashBoardRepository dashBoardRepository;

  @Transactional(readOnly = true)
  public List<DashBoardCalenderDTO> getDashBoardCalender(YearMonth ym){

    LocalDate startDate = ym.atDay(1);
    LocalDate endDate = YearMonth.from(startDate).atEndOfMonth();

    List<DashBoardCalenderDTO> dashBoardCalender = dashBoardRepository.findDashBoardAuctions(startDate.atStartOfDay(), endDate.atStartOfDay());

    Map<LocalDate, DashBoardCalenderDTO> calenderMappingMap = saveCalenderMap(dashBoardCalender);

    return generateCalender(calenderMappingMap, startDate, endDate);
  }

  public Map<LocalDate, DashBoardCalenderDTO> saveCalenderMap(List<DashBoardCalenderDTO> dashBoardCalender){ //db에서 검색된 날짜를 map에 저장
    return dashBoardCalender.stream().collect(Collectors.toMap(
        DashBoardCalenderDTO::date,
        data -> data
    ));
  }

  public List<DashBoardCalenderDTO> generateCalender(Map<LocalDate, DashBoardCalenderDTO> calenderMapping, LocalDate start, LocalDate end){ //검색되지 않은 날짜들 순서에 맞게 채우기
    List<DashBoardCalenderDTO> resultData = new ArrayList<>();

    for(LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
      resultData.add(calenderMapping.getOrDefault(date, new DashBoardCalenderDTO(date, 0, 0)));
    }

    return resultData;
  }
}
