package com.lupseiv.supplements.intakelog;

import com.lupseiv.supplements.common.ConflictException;
import com.lupseiv.supplements.common.NotFoundException;
import com.lupseiv.supplements.intakelog.dto.CreateIntakeLogRequest;
import com.lupseiv.supplements.intakelog.dto.DayLogsResponse;
import com.lupseiv.supplements.intakelog.dto.IntakeLogResponse;
import com.lupseiv.supplements.usersupplement.UserSupplement;
import com.lupseiv.supplements.usersupplement.UserSupplementRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IntakeLogService {

    private final IntakeLogRepository intakeLogRepository;
    private final UserSupplementRepository userSupplementRepository;

    public IntakeLogService(IntakeLogRepository intakeLogRepository,
                            UserSupplementRepository userSupplementRepository) {
        this.intakeLogRepository = intakeLogRepository;
        this.userSupplementRepository = userSupplementRepository;
    }

    public IntakeLogResponse log(CreateIntakeLogRequest request) {
        UserSupplement userSupplement = userSupplementRepository.findById(request.userSupplementId())
                .orElseThrow(() -> new NotFoundException(
                        "User supplement %d not found".formatted(request.userSupplementId())));
        if (!userSupplement.isActive()) {
            throw new ConflictException("Supplement is not active, toggle \"I take this\" on first");
        }
        Instant takenAt = request.takenAt() != null ? request.takenAt() : Instant.now();
        IntakeLog saved = intakeLogRepository.save(new IntakeLog(userSupplement, takenAt));
        return IntakeLogResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<DayLogsResponse> history(int days) {
        LocalDate fromDate = LocalDate.now().minusDays(Math.max(days, 1) - 1L);
        Map<LocalDate, List<IntakeLogResponse>> byDay = new LinkedHashMap<>();
        for (IntakeLog log : intakeLogRepository.findSince(fromDate)) {
            byDay.computeIfAbsent(log.getLogDate(), d -> new java.util.ArrayList<>())
                    .add(IntakeLogResponse.from(log));
        }
        return byDay.entrySet().stream()
                .map(entry -> new DayLogsResponse(entry.getKey(), List.copyOf(entry.getValue())))
                .toList();
    }

    public void delete(Long id) {
        if (!intakeLogRepository.existsById(id)) {
            throw new NotFoundException("Intake log %d not found".formatted(id));
        }
        intakeLogRepository.deleteById(id);
    }
}
