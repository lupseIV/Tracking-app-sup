package com.lupseiv.supplements.usersupplement.dto;

import com.lupseiv.supplements.usersupplement.UserSupplement;
import java.time.LocalDate;

public record UserSupplementResponse(
        Long id,
        Long supplementId,
        String name,
        String typicalDosage,
        LocalDate addedDate,
        boolean active
) {
    public static UserSupplementResponse from(UserSupplement userSupplement) {
        return new UserSupplementResponse(
                userSupplement.getId(),
                userSupplement.getSupplement().getId(),
                userSupplement.getSupplement().getName(),
                userSupplement.getSupplement().getTypicalDosage(),
                userSupplement.getAddedDate(),
                userSupplement.isActive()
        );
    }
}
