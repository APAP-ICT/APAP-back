package com.example.apapbackend.dashBoard.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record DashBoardBottomResponse(
    Map<LocalDate, List<Entry<String, Long>>> getTopLabelsForLast3Days,
    Map<String, Long> getTopThreeLabelsForLast3Days
) {

}
