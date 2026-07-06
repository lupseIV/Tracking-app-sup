package com.lupseiv.supplements.intakelog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.lupseiv.supplements.BaseControllerIntegrationTest;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class IntakeLogControllerIT extends BaseControllerIntegrationTest {

    private int activateAndGetUserSupplementId(long supplementId) throws Exception {
        String response = mockMvc.perform(post("/api/user-supplements/" + supplementId))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }

    @Test
    void logIntakeThenHistoryGroupsByDay() throws Exception {
        int userSupplementId = activateAndGetUserSupplementId(1);

        mockMvc.perform(post("/api/intake-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userSupplementId\": " + userSupplementId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplementName").value("Vitamin D3"))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()));

        mockMvc.perform(get("/api/intake-logs").param("days", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[0].logs.length()").value(1))
                .andExpect(jsonPath("$[0].logs[0].supplementName").value("Vitamin D3"));
    }

    @Test
    void deletingALogRemovesItFromHistory() throws Exception {
        int userSupplementId = activateAndGetUserSupplementId(1);
        String response = mockMvc.perform(post("/api/intake-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userSupplementId\": " + userSupplementId + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        int logId = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/intake-logs/" + logId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/intake-logs").param("days", "1"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void logRejectsMissingUserSupplementId() throws Exception {
        mockMvc.perform(post("/api/intake-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logReturns409ForDeactivatedSupplement() throws Exception {
        int userSupplementId = activateAndGetUserSupplementId(1);
        mockMvc.perform(delete("/api/user-supplements/1")).andExpect(status().isNoContent());

        mockMvc.perform(post("/api/intake-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userSupplementId\": " + userSupplementId + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    void logReturns404ForUnknownUserSupplement() throws Exception {
        mockMvc.perform(post("/api/intake-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userSupplementId\": 9999}"))
                .andExpect(status().isNotFound());
    }
}
