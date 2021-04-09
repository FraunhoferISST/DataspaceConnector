package de.fraunhofer.isst.dataspaceconnector.controller;

import java.net.URI;

import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerIT {

    @MockBean
    ConnectorService connectorService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getPublicSelfDescription_nothing_returnValidDescription() throws Exception {
        /* ARRANGE */
        final var connector = getConnectorWithoutResources();
        Mockito.when(connectorService.getConnectorWithoutResources()).thenReturn(connector);

        /* ACT */
        final var result = mockMvc.perform(get("/")).andExpect(status().isOk()).andReturn();

        /* ASSERT */
        assertDoesNotThrow( () -> new Serializer().deserialize(result.getResponse().getContentAsString(), BaseConnector.class));
        assertEquals(connector.toRdf(), result.getResponse().getContentAsString());
    }

    private BaseConnector getConnectorWithoutResources() {
        return new BaseConnectorBuilder()
                ._curator_(URI.create("https://someBody"))
                ._maintainer_(URI.create("https:://someoneElse"))
                ._outboundModelVersion_("4.0.0")
                ._inboundModelVersion_(de.fraunhofer.iais.eis.util.Util.asList("4.0.0"))
                ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                .build();
    }
}
