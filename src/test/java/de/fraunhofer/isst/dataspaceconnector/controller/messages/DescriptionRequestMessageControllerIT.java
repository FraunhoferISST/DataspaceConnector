package de.fraunhofer.isst.dataspaceconnector.controller.messages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DescriptionRequestMessageControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void sendDescriptionRequestMessage_anything_unauthorized() throws Exception {
        mockMvc.perform(post("/api/ids/description")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void sendDescriptionRequestMessage_validInput_() throws Exception {
        /* ARRANGE */

        /* ACT && ASSERT */
        mockMvc.perform(post("/api/ids/description")
            .param("recipient", "https://localhost:8080/")
        ).andExpect(status().isOk());
    }
}
