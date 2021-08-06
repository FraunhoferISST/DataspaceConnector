package io.dataspaceconnector.controller.message;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppRequestMessageControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendMessage_validInput_willReturn200() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someRecipient");
        final var app = URI.create("https://someApp");

        /* ACT && ASSERT */
        mockMvc.perform(post("/api/ids/app")
                                .param("recipient", recipient.toString())
                                .param("app", app.toString()))
               .andExpect(status().isOk());
    }
}
