package com.lupseiv.supplements.intakelog.dto;

import java.time.LocalDate;
import java.util.List;

public record DayLogsResponse(
        LocalDate date,
        List<IntakeLogResponse> logs
) {
}
