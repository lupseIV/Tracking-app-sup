package com.lupseiv.supplements.supplement;

import com.lupseiv.supplements.common.ConflictException;
import com.lupseiv.supplements.common.NotFoundException;
import com.lupseiv.supplements.supplement.dto.BuyLinkDto;
import com.lupseiv.supplements.supplement.dto.CreateSupplementRequest;
import com.lupseiv.supplements.supplement.dto.SupplementResponse;
import com.lupseiv.supplements.usersupplement.UserSupplementRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SupplementService {

    private final SupplementRepository supplementRepository;
    private final UserSupplementRepository userSupplementRepository;

    public SupplementService(SupplementRepository supplementRepository,
                             UserSupplementRepository userSupplementRepository) {
        this.supplementRepository = supplementRepository;
        this.userSupplementRepository = userSupplementRepository;
    }

    @Transactional(readOnly = true)
    public List<SupplementResponse> search(String search) {
        String normalized = (search == null || search.isBlank()) ? null : search.trim();
        Set<Long> activeIds = userSupplementRepository.findActiveSupplementIds();
        return supplementRepository.search(normalized).stream()
                .map(supplement -> SupplementResponse.from(supplement, activeIds.contains(supplement.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public SupplementResponse getById(Long id) {
        Supplement supplement = findSupplement(id);
        boolean tracked = userSupplementRepository.findBySupplementId(id)
                .map(us -> us.isActive())
                .orElse(false);
        return SupplementResponse.from(supplement, tracked);
    }

    public SupplementResponse createCustom(CreateSupplementRequest request) {
        Supplement supplement = new Supplement(
                request.name().trim(),
                request.description() == null ? "" : request.description().trim(),
                request.typicalDosage() == null ? "" : request.typicalDosage().trim(),
                request.category() == null || request.category().isBlank() ? "Custom" : request.category().trim(),
                true,
                request.benefits() == null ? List.of() : request.benefits(),
                request.buyLinks() == null ? List.of()
                        : request.buyLinks().stream()
                                .map(dto -> new BuyLink(dto.storeName(), dto.url()))
                                .toList()
        );
        return SupplementResponse.from(supplementRepository.save(supplement), false);
    }

    public void deleteCustom(Long id) {
        Supplement supplement = findSupplement(id);
        if (!supplement.isCustom()) {
            throw new ConflictException("Built-in supplements cannot be deleted");
        }
        supplementRepository.delete(supplement);
    }

    private Supplement findSupplement(Long id) {
        return supplementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplement %d not found".formatted(id)));
    }
}
