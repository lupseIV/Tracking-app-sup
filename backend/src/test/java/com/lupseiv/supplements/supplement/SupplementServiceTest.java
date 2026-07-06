package com.lupseiv.supplements.supplement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lupseiv.supplements.common.ConflictException;
import com.lupseiv.supplements.common.NotFoundException;
import com.lupseiv.supplements.supplement.dto.CreateSupplementRequest;
import com.lupseiv.supplements.supplement.dto.SupplementResponse;
import com.lupseiv.supplements.usersupplement.UserSupplementRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplementServiceTest {

    @Mock
    private SupplementRepository supplementRepository;

    @Mock
    private UserSupplementRepository userSupplementRepository;

    @InjectMocks
    private SupplementService supplementService;

    @Test
    void deleteCustomRejectsBuiltInSupplements() {
        Supplement builtIn = new Supplement("Vitamin D3", "desc", "1000 IU", "Vitamin", false, List.of(), List.of());
        when(supplementRepository.findById(1L)).thenReturn(Optional.of(builtIn));

        assertThatThrownBy(() -> supplementService.deleteCustom(1L))
                .isInstanceOf(ConflictException.class);
        verify(supplementRepository, never()).delete(any());
    }

    @Test
    void deleteCustomDeletesCustomSupplements() {
        Supplement custom = new Supplement("My Blend", "desc", "1 scoop", "Custom", true, List.of(), List.of());
        when(supplementRepository.findById(5L)).thenReturn(Optional.of(custom));

        supplementService.deleteCustom(5L);

        verify(supplementRepository).delete(custom);
    }

    @Test
    void getByIdThrowsNotFoundForUnknownId() {
        when(supplementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplementService.getById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createCustomDefaultsBlankCategoryAndMarksCustom() {
        when(supplementRepository.save(any(Supplement.class))).thenAnswer(inv -> inv.getArgument(0));

        SupplementResponse response = supplementService.createCustom(
                new CreateSupplementRequest("My Blend", null, null, "  ", null, null));

        assertThat(response.name()).isEqualTo("My Blend");
        assertThat(response.category()).isEqualTo("Custom");
        assertThat(response.isCustom()).isTrue();
        assertThat(response.benefits()).isEmpty();
        assertThat(response.buyLinks()).isEmpty();
    }
}
