//package io.dataspaceconnector;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class OpenApiIT {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Test
//    @WithMockUser("ADMIN")
//    public void compare_openApi_equals() throws Exception {
//        /* ASSERT */
//        final var result = mockMvc.perform(get("/v3/api-docs.yaml")).andExpect(status().isOk()).andReturn();
//
//        /* ASSERT */
//        final var currentOpenApi = Files.readString(Path.of("openapi.yaml"));
//        if(!currentOpenApi.equals(result.getResponse().getContentAsString())) {
//            Files.writeString(Path.of("openapi-new.yaml"), result.getResponse().getContentAsString());
//        }
//
//        assertEquals(currentOpenApi, result.getResponse().getContentAsString());
//    }
//}
