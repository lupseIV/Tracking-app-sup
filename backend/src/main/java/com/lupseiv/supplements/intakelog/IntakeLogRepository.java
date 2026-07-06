package com.lupseiv.supplements.intakelog;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntakeLogRepository extends JpaRepository<IntakeLog, Long> {

    @Query("""
            SELECT l FROM IntakeLog l
            JOIN FETCH l.userSupplement us
            JOIN FETCH us.supplement
            WHERE l.logDate >= :fromDate
            ORDER BY l.logDate DESC, l.takenAt DESC
            """)
    List<IntakeLog> findSince(@Param("fromDate") LocalDate fromDate);
}
