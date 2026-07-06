package com.lupseiv.supplements.intakelog.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateIntakeLogRequest(
        @NotNull Long userSupplementId,
        Instant takenAt
) {
}
