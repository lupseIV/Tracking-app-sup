package com.lupseiv.supplements.usersupplement;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserSupplementRepository extends JpaRepository<UserSupplement, Long> {

    Optional<UserSupplement> findBySupplementId(Long supplementId);

    @Query("SELECT us FROM UserSupplement us JOIN FETCH us.supplement WHERE us.active = true ORDER BY us.supplement.name")
    List<UserSupplement> findAllActive();

    @Query("SELECT us.supplement.id FROM UserSupplement us WHERE us.active = true")
    Set<Long> findActiveSupplementIds();
}
