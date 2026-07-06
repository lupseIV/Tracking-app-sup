package com.lupseiv.supplements.supplement.dto;

import com.lupseiv.supplements.supplement.Supplement;
import java.util.List;

public record SupplementResponse(
        Long id,
        String name,
        String description,
        String typicalDosage,
        String category,
        boolean isCustom,
        List<String> benefits,
        List<BuyLinkDto> buyLinks,
        boolean tracked
) {
    public static SupplementResponse from(Supplement supplement, boolean tracked) {
        return new SupplementResponse(
                supplement.getId(),
                supplement.getName(),
                supplement.getDescription(),
                supplement.getTypicalDosage(),
                supplement.getCategory(),
                supplement.isCustom(),
                List.copyOf(supplement.getBenefits()),
                supplement.getBuyLinks().stream()
                        .map(link -> new BuyLinkDto(link.getStoreName(), link.getUrl()))
                        .toList(),
                tracked
        );
    }
}
