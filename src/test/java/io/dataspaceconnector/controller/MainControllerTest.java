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
package io.dataspaceconnector.controller;

import javax.validation.ConstraintViolationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.SecurityProfile;
import io.dataspaceconnector.common.ids.ConnectorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class MainControllerTest {

    @MockBean
    private ConnectorService connectorService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getPublicSelfDescription_nothing_returnDescriptionWithOutDescription() throws Exception {
        /* ARRANGE */
        final var connector = new BaseConnectorBuilder()
                ._curator_(URI.create("someCurator"))
                ._outboundModelVersion_("9999")
                ._maintainer_(URI.create("someMaintainer"))
                ._inboundModelVersion_(new ArrayList<>(Arrays.asList("9991", "9992")))
                ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                ._hasDefaultEndpoint_(new ConnectorEndpointBuilder()
                        ._accessURL_(URI.create("https://accessUrl"))
                        .build())
                .build();
        Mockito.doReturn(connector).when(connectorService).getConnectorWithoutResources();

        /* ACT */
        final var result = mockMvc.perform(get("/")).andExpect(status().isOk()).andReturn();

        /* ASSERT */
        assertEquals(connector.toRdf(), result.getResponse().getContentAsString());
    }

    @Test
    public void getPublicSelfDescription_serviceFails_InternalServerError() throws Exception {
        /* ARRANGE */
        Mockito.doThrow(ConstraintViolationException.class).when(connectorService).getConnectorWithoutResources();

        /* ACT */
        final var result = mockMvc.perform(get("/"))
                .andExpect(status().isInternalServerError()).andReturn();

        /* ASSERT */
        assertEquals("application/json", result.getResponse().getContentType());
        assertEquals("{\"message\":\"An error occurred. Please try again later.\"}",
                result.getResponse().getContentAsString());
    }

    /**
     * getPrivateSelfDescription
     */

    @Test
    @WithMockUser("ADMIN")
    public void getPrivateSelfDescription_nothing_returnDescription() throws Exception {
        /* ARRANGE */
        final var connector = new BaseConnectorBuilder()
                ._curator_(URI.create("someCurator"))
                ._outboundModelVersion_("9999")
                ._maintainer_(URI.create("someMaintainer"))
                ._inboundModelVersion_(new ArrayList<>(Arrays.asList("9991", "9992")))
                ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                ._hasDefaultEndpoint_(new ConnectorEndpointBuilder()
                        ._accessURL_(URI.create("https://accessUrl"))
                        .build())
                .build();
        Mockito.doReturn(connector).when(connectorService).getConnectorWithOfferedResources();

        /* ACT */
        final var result =
                mockMvc.perform(get("/api/connector")).andExpect(status().isOk()).andReturn();

        /* ASSERT */
        assertEquals(connector.toRdf(), result.getResponse().getContentAsString());
    }


    @Test
    @WithMockUser("ADMIN")
    public void getPrivateSelfDescription_serviceFails_InternalServerError() throws Exception {
        /* ARRANGE */
        Mockito.doThrow(ConstraintViolationException.class).when(connectorService).getConnectorWithOfferedResources();

        /* ACT */
        final var result = mockMvc.perform(get("/api/connector"))
                .andExpect(status().isInternalServerError()).andReturn();

        /* ASSERT */
        assertEquals("application/json", result.getResponse().getContentType());
        assertEquals("{\"message\":\"An error occurred. Please try again later.\"}",
                result.getResponse().getContentAsString());
    }

    /**
     * root
     */

    @Test
    @WithMockUser("ADMIN")
    public void root_nothing_returnApiEntryPoint() throws Exception {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        final var result = mockMvc.perform(get("/api")).andExpect(status().isOk()).andReturn();

        assertEquals("{\"_links\":{\"self\":{\"href\":\"http://localhost/api\"}," +
                        "\"agreements\":{\"href\":\"http://localhost/api/agreements{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"apps\":{\"href\":\"http://localhost/api/apps{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"appstores\":{\"href\":\"http://localhost/api/appstores{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"artifacts\":{\"href\":\"http://localhost/api/artifacts{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"brokers\":{\"href\":\"http://localhost/api/brokers{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"catalogs\":{\"href\":\"http://localhost/api/catalogs{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"contracts\":{\"href\":\"http://localhost/api/contracts{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"datasources\":{\"href\":\"http://localhost/api/datasources{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"endpoints\":{\"href\":\"http://localhost/api/endpoints{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"offers\":{\"href\":\"http://localhost/api/offers{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"representations\":{\"href\":\"http://localhost/api/representations{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"routes\":{\"href\":\"http://localhost/api/routes{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"requests\":{\"href\":\"http://localhost/api/requests{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"rules\":{\"href\":\"http://localhost/api/rules{?page,size}\"," +
                        "\"templated\":true}," +
                        "\"subscriptions\":{\"href\":\"http://localhost/api/subscriptions{?page,size}\"," +
                        "\"templated\":true}}}",
                result.getResponse().getContentAsString());
        assertEquals("application/hal+json", result.getResponse().getContentType());
    }
}
