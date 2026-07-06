package com.lupseiv.supplements.usersupplement;

import com.lupseiv.supplements.supplement.Supplement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "user_supplement")
public class UserSupplement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplement_id", nullable = false, unique = true)
    private Supplement supplement;

    @Column(name = "added_date", nullable = false)
    private LocalDate addedDate;

    @Column(nullable = false)
    private boolean active;

    /** Phase 2 sync seam — unused in Phase 1. */
    @Column(name = "device_id", length = 64)
    private String deviceId;

    /** Phase 2 sync seam — bumped on every change for future delta sync. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserSupplement() {
        // for JPA
    }

    public UserSupplement(Supplement supplement) {
        this.supplement = supplement;
        this.addedDate = LocalDate.now();
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Supplement getSupplement() {
        return supplement;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public boolean isActive() {
        return active;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
