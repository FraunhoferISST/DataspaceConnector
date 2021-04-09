package de.fraunhofer.isst.dataspaceconnector.services.messages;

import java.net.URI;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.EntityUpdateService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ArtifactRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractAgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.DescriptionRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.TemplateBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {MessageService.class})
public class MessageServiceTest {

    @MockBean
    DescriptionRequestService descService;

    @MockBean
    ContractRequestService contractRequestService;

    @MockBean
    ContractAgreementService contractAgreementService;

    @MockBean
    ArtifactRequestService artifactRequestService;

    @MockBean
    DeserializationService deserializationService;

    @MockBean
    TemplateBuilder<RequestedResource, RequestedResourceDesc> templateBuilder;

    @MockBean
    ConnectorService connectorService;

    @MockBean
    EntityResolver entityResolver;

    @MockBean
    ObjectMapper objectMapper;

    @MockBean
    EntityUpdateService updateService;

    @Autowired
    MessageService service;

    @Test
    public void sendDescriptionRequestMessage_validSelfDescriptionInput_returnValidResponse() {
        /* ARRANGE */
        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var desc = new DescriptionRequestMessageDesc(recipient, null);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(descService.sendMessage(Mockito.eq(desc), Mockito.eq(""))).thenReturn(response);

        /* ACT */
        final var result = service.sendDescriptionRequestMessage(recipient, null);

        /* ARRANGE */
        assertEquals(response, result);
    }

    @Test
    public void validateDescriptionResponseMessage_validResponse_returnTrue() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(descService.isValidResponseType(Mockito.eq(response))).thenReturn(true);

        /* ACT */
        final var result = service.validateDescriptionResponseMessage(response);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void validateDescriptionResponseMessage_invalidResponse_returnFalse() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some invalid header");
        response.put("body", "some invalid body");

        Mockito.when(descService.isValidResponseType(Mockito.eq(response))).thenReturn(false);

        /* ACT */
        final var result = service.validateDescriptionResponseMessage(response);

        /* ASSERT */
        assertFalse(result);
    }
}
