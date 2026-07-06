package com.lupseiv.supplements.supplement;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplementRepository extends JpaRepository<Supplement, Long> {

    @Query("""
            SELECT s FROM Supplement s
            WHERE :search IS NULL
               OR lower(s.name) LIKE lower(concat('%', :search, '%'))
               OR lower(s.category) LIKE lower(concat('%', :search, '%'))
            ORDER BY s.name
            """)
    List<Supplement> search(@Param("search") String search);
}
