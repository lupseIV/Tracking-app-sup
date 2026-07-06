package com.lupseiv.supplements.usersupplement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lupseiv.supplements.BaseControllerIntegrationTest;
import org.junit.jupiter.api.Test;

class UserSupplementControllerIT extends BaseControllerIntegrationTest {

    @Test
    void checklistIsEmptyInitially() throws Exception {
        mockMvc.perform(get("/api/user-supplements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void activateAddsSupplementToChecklistAndMarksItTracked() throws Exception {
        mockMvc.perform(post("/api/user-supplements/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplementId").value(1))
                .andExpect(jsonPath("$.name").value("Vitamin D3"))
                .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(get("/api/user-supplements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/supplements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tracked").value(true));
    }

    @Test
    void deactivateRemovesFromChecklistAndCanBeReactivated() throws Exception {
        mockMvc.perform(post("/api/user-supplements/1")).andExpect(status().isCreated());
        mockMvc.perform(delete("/api/user-supplements/1")).andExpect(status().isNoContent());

        mockMvc.perform(get("/api/user-supplements"))
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(post("/api/user-supplements/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void activateReturns404ForUnknownSupplement() throws Exception {
        mockMvc.perform(post("/api/user-supplements/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivateReturns404WhenNotTracked() throws Exception {
        mockMvc.perform(delete("/api/user-supplements/1"))
                .andExpect(status().isNotFound());
    }
}
