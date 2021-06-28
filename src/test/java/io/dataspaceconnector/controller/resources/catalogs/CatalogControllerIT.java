package io.dataspaceconnector.controller.resources.catalogs;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser("ADMIN")
    void create_validInput_returnNew() throws Exception {
        mockMvc.perform(post("/api/catalogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
               .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("ADMIN")
    void getAll_validInput_returnObj() throws Exception {
        for(int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/catalogs")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}"))
                   .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/catalogs")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("ADMIN")
    void get_validInput_returnObj() throws Exception {
        final var newObject =
        mockMvc.perform(post("/api/catalogs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
               .andExpect(status().isCreated()).andReturn();

        final var newObj = newObject.getResponse().getHeader("Location");

        mockMvc.perform(get(URI.create(newObj).getPath())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("ADMIN")
    void update_validInput_returnAcknowledge() throws Exception {
        final var newObject =
                mockMvc.perform(post("/api/catalogs")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}"))
                       .andExpect(status().isCreated()).andReturn();

        final var newObj = newObject.getResponse().getHeader("Location");

        mockMvc.perform(put(URI.create(newObj).getPath())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser("ADMIN")
    void delete_validInput_returnAcknowledge() throws Exception {
        final var newObject =
                mockMvc.perform(post("/api/catalogs")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}"))
                       .andExpect(status().isCreated()).andReturn();

        final var newObj = newObject.getResponse().getHeader("Location");

        mockMvc.perform(delete(URI.create(newObj).getPath()))
               .andExpect(status().isNoContent());
    }
}
