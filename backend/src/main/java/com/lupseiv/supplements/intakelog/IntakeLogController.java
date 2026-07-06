package com.lupseiv.supplements.intakelog;

import com.lupseiv.supplements.intakelog.dto.CreateIntakeLogRequest;
import com.lupseiv.supplements.intakelog.dto.DayLogsResponse;
import com.lupseiv.supplements.intakelog.dto.IntakeLogResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/intake-logs")
public class IntakeLogController {

    private final IntakeLogService intakeLogService;

    public IntakeLogController(IntakeLogService intakeLogService) {
        this.intakeLogService = intakeLogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IntakeLogResponse log(@Valid @RequestBody CreateIntakeLogRequest request) {
        return intakeLogService.log(request);
    }

    @GetMapping
    public List<DayLogsResponse> history(@RequestParam(defaultValue = "30") int days) {
        return intakeLogService.history(days);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        intakeLogService.delete(id);
    }
}
