package com.lupseiv.supplements.usersupplement;

import com.lupseiv.supplements.common.NotFoundException;
import com.lupseiv.supplements.supplement.Supplement;
import com.lupseiv.supplements.supplement.SupplementRepository;
import com.lupseiv.supplements.usersupplement.dto.UserSupplementResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserSupplementService {

    private final UserSupplementRepository userSupplementRepository;
    private final SupplementRepository supplementRepository;

    public UserSupplementService(UserSupplementRepository userSupplementRepository,
                                 SupplementRepository supplementRepository) {
        this.userSupplementRepository = userSupplementRepository;
        this.supplementRepository = supplementRepository;
    }

    @Transactional(readOnly = true)
    public List<UserSupplementResponse> getActive() {
        return userSupplementRepository.findAllActive().stream()
                .map(UserSupplementResponse::from)
                .toList();
    }

    /** Toggles "I take this" on; reactivates the existing row if it was toggled off before. */
    public UserSupplementResponse activate(Long supplementId) {
        UserSupplement userSupplement = userSupplementRepository.findBySupplementId(supplementId)
                .orElseGet(() -> {
                    Supplement supplement = supplementRepository.findById(supplementId)
                            .orElseThrow(() -> new NotFoundException(
                                    "Supplement %d not found".formatted(supplementId)));
                    return new UserSupplement(supplement);
                });
        userSupplement.setActive(true);
        return UserSupplementResponse.from(userSupplementRepository.save(userSupplement));
    }

    /** Toggles "I take this" off; intake history is kept. */
    public void deactivate(Long supplementId) {
        UserSupplement userSupplement = userSupplementRepository.findBySupplementId(supplementId)
                .orElseThrow(() -> new NotFoundException(
                        "Supplement %d is not tracked".formatted(supplementId)));
        userSupplement.setActive(false);
        userSupplementRepository.save(userSupplement);
    }
}
