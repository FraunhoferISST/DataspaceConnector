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
package io.dataspaceconnector.controller.policy;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.ids.DeserializationService;
import org.apache.commons.codec.CharEncoding;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class ExampleControllerIT {

    @SpyBean
    private DeserializationService deserializationService;

    @Autowired
    private MockMvc mockMvc;

    /**
     * getPolicyPattern
     */

    @Test
    @WithMockUser("ADMIN")
    public void getPolicyPattern_AllowAccess_returnPatternDescriptor() throws Exception {
        final var permission = new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Allow Data Usage")))
                ._description_(Util.asList(new TypedLiteral("provide-access")))
                ._action_(Util.asList(Action.USE))
                .build();
        final var expectedResult = new JSONObject() {{
            put("value", "PROVIDE_ACCESS");
        }};

        Mockito.doReturn(permission).when(deserializationService).getRule(eq(permission.toRdf()));

        final var result = mockMvc.perform(post("/api/examples/validation")
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .characterEncoding(CharEncoding.UTF_8)
                                                                .content(permission.toRdf()))
                                            .andExpect(status().isOk())
                                            .andReturn();
        final var resultString = result.getResponse().getContentAsString();

        assertEquals(expectedResult.toString(), resultString);
    }
}
