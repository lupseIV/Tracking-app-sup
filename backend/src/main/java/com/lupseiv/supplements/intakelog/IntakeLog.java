package com.lupseiv.supplements.intakelog;

import com.lupseiv.supplements.usersupplement.UserSupplement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Table(name = "intake_log")
public class IntakeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_supplement_id", nullable = false)
    private UserSupplement userSupplement;

    @Column(name = "taken_at", nullable = false)
    private Instant takenAt;

    /** Denormalized day (system zone at logging time) for grouping history. */
    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    /** Phase 2 sync seam — unused in Phase 1. */
    @Column(name = "device_id", length = 64)
    private String deviceId;

    /** Phase 2 sync seam. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected IntakeLog() {
        // for JPA
    }

    public IntakeLog(UserSupplement userSupplement, Instant takenAt) {
        this.userSupplement = userSupplement;
        this.takenAt = takenAt;
        this.logDate = takenAt.atZone(ZoneId.systemDefault()).toLocalDate();
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public UserSupplement getUserSupplement() {
        return userSupplement;
    }

    public Instant getTakenAt() {
        return takenAt;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
