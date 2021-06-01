/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.BasicAuthenticationBuilder;
import de.fraunhofer.iais.eis.ConfigurationModelBuilder;
import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.KeyType;
import de.fraunhofer.iais.eis.LogLevel;
import de.fraunhofer.iais.eis.ProxyBuilder;
import de.fraunhofer.iais.eis.PublicKeyBuilder;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.services.ids.DeserializationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ExampleControllerTest {

    @MockBean
    private DeserializationService deserializationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getConnectorConfiguration_notAuthorized_notAuthorized() throws Exception {
        mockMvc.perform(get("/api/examples/configuration")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("ADMIN")
    public void getConnectorConfiguration_nothing_sampleConfig() throws Exception {
        /* ARRANGE */
        final var expect = new ConfigurationModelBuilder(URI.create("configId"))
                ._configurationModelLogLevel_(LogLevel.NO_LOGGING)
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._connectorProxy_(Util.asList(
                        new ProxyBuilder(URI.create("proxiId"))
                                ._noProxy_(new ArrayList<>(Collections.singletonList(
                                        URI.create("https://localhost:8080/"))))
                                ._proxyAuthentication_(
                                        new BasicAuthenticationBuilder(URI.create("basicAuthId")).build())
                                ._proxyURI_(URI.create(
                                        "proxy.dortmund.isst.fraunhofer.de:3128"))
                                .build()))
                ._connectorStatus_(ConnectorStatus.CONNECTOR_ONLINE)
                ._connectorDescription_(
                        new BaseConnectorBuilder(URI.create("connectorId"))
                                ._maintainer_(URI.create("https://example.com"))
                                ._curator_(URI.create("https://example.com"))
                                ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                                ._outboundModelVersion_("4.0.0")
                                ._inboundModelVersion_(Util.asList("4.0.0"))
                                ._title_(Util.asList(
                                        new TypedLiteral("Dataspace Connector")))
                                ._description_(Util.asList(new TypedLiteral(
                                        "IDS Connector with static "
                                        + "example resources hosted by the Fraunhofer ISST.")))
                                ._version_("v3.0.0")
                                ._publicKey_(
                                        new PublicKeyBuilder(URI.create("keyId"))
                                                ._keyType_(KeyType.RSA)
                                                ._keyValue_(
                                                        "Your daps token here.".getBytes(
                                                                StandardCharsets.UTF_8))
                                                .build())
                                ._hasDefaultEndpoint_(
                                        new ConnectorEndpointBuilder(URI.create("endpointId"))
                                                ._accessURL_(URI.create("/api/ids/data"))
                                                .build())
                                .build())
                ._keyStore_(URI.create("file:///conf/keystore.p12"))
                ._trustStore_(URI.create("file:///conf/truststore.p12"))
                .build();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(get("/api/examples/configuration")).andExpect(status().isOk()).andReturn();
        assertEquals(expect.toRdf(), result.getResponse().getContentAsString());
    }

    /**
     * getPolicyPattern
     */

    @Test
    public void getPolicyPattern_notAuthorized_notAuthorized() throws Exception {
        mockMvc.perform(get("/api/examples/validation")).andExpect(status().isUnauthorized());
    }
}
