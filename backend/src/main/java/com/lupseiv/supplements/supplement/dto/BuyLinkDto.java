package com.lupseiv.supplements.supplement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BuyLinkDto(
        @NotBlank @Size(max = 60) String storeName,
        @NotBlank @Size(max = 500) String url
) {
}
