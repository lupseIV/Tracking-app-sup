package com.lupseiv.supplements.intakelog.dto;

import com.lupseiv.supplements.intakelog.IntakeLog;
import java.time.Instant;
import java.time.LocalDate;

public record IntakeLogResponse(
        Long id,
        Long userSupplementId,
        Long supplementId,
        String supplementName,
        Instant takenAt,
        LocalDate date
) {
    public static IntakeLogResponse from(IntakeLog log) {
        return new IntakeLogResponse(
                log.getId(),
                log.getUserSupplement().getId(),
                log.getUserSupplement().getSupplement().getId(),
                log.getUserSupplement().getSupplement().getName(),
                log.getTakenAt(),
                log.getLogDate()
        );
    }
}
