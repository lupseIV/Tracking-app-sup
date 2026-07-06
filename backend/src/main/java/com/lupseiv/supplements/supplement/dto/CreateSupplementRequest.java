package com.lupseiv.supplements.supplement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateSupplementRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 1000) String description,
        @Size(max = 255) String typicalDosage,
        @Size(max = 60) String category,
        List<@NotBlank @Size(max = 255) String> benefits,
        List<@Valid BuyLinkDto> buyLinks
) {
}
