package io.dataspaceconnector.controller.resource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RepresentationControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser("ADMIN")
    void create_validInput_returnNew() throws Exception {
        /* ARRANGE */
        // nothing to do here

        /* ACT */
        final var response = mockMvc.perform(post("/api/representations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andReturn();

        /* ASSERT */
        assertNotNull(response);
        assertEquals(201, response.getResponse().getStatus());
    }
}
