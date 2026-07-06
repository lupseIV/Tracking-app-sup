package com.lupseiv.supplements.supplement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.lupseiv.supplements.BaseControllerIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class SupplementControllerIT extends BaseControllerIntegrationTest {

    @Test
    void listReturnsSeededCatalogOrderedByName() throws Exception {
        mockMvc.perform(get("/api/supplements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(18))
                .andExpect(jsonPath("$[0].name").value("Ashwagandha"));
    }

    @Test
    void searchFiltersByNameOrCategory() throws Exception {
        mockMvc.perform(get("/api/supplements").param("search", "magnesium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Magnesium"));

        mockMvc.perform(get("/api/supplements").param("search", "mineral"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void getByIdReturnsBenefitsAndBuyLinks() throws Exception {
        mockMvc.perform(get("/api/supplements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vitamin D3"))
                .andExpect(jsonPath("$.isCustom").value(false))
                .andExpect(jsonPath("$.tracked").value(false))
                .andExpect(jsonPath("$.benefits.length()").value(3))
                .andExpect(jsonPath("$.buyLinks.length()").value(4))
                .andExpect(jsonPath("$.buyLinks[0].storeName").value("Amazon"))
                .andExpect(jsonPath("$.buyLinks[3].storeName").value("Google Shopping"));
    }

    @Test
    void getByIdReturns404ForUnknownSupplement() throws Exception {
        mockMvc.perform(get("/api/supplements/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCustomThenDeleteWorks() throws Exception {
        String body = """
                {
                  "name": "My Custom Blend",
                  "category": "Custom",
                  "description": "My own mix",
                  "typicalDosage": "1 scoop daily",
                  "benefits": ["Feels good"],
                  "buyLinks": [{"storeName": "Local shop", "url": "https://example.com"}]
                }
                """;
        String response = mockMvc.perform(post("/api/supplements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isCustom").value(true))
                .andExpect(jsonPath("$.benefits[0]").value("Feels good"))
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/supplements/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/supplements/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBuiltInReturns409() throws Exception {
        mockMvc.perform(delete("/api/supplements/1"))
                .andExpect(status().isConflict());
    }
}
