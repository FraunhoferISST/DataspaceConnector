/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
