package com.lupseiv.supplements.supplement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lupseiv.supplements.supplement.dto.SupplementResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplementController.class)
class SupplementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupplementService supplementService;

    @Test
    void listReturnsSupplementsFromService() throws Exception {
        SupplementResponse response = new SupplementResponse(
                1L, "Vitamin D3", "desc", "1000 IU", "Vitamin", false, List.of("Bones"), List.of(), false);
        when(supplementService.search(any())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/supplements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Vitamin D3"));
    }

    @Test
    void createRejectsBlankNameWithValidationDetails() throws Exception {
        mockMvc.perform(post("/api/supplements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details[0]").exists());
    }
}
