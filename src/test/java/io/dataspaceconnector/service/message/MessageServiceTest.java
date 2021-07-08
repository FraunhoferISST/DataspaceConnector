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
/*
package io.dataspaceconnector.services.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import io.dataspaceconnector.model.resources.RequestedResource;
import io.dataspaceconnector.model.resources.RequestedResourceDesc;
import io.dataspaceconnector.model.messages.ArtifactRequestMessageDesc;
import io.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import io.dataspaceconnector.model.messages.ContractRequestMessageDesc;
import io.dataspaceconnector.model.messages.DescriptionRequestMessageDesc;
import io.dataspaceconnector.services.EntityResolver;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.services.messages.types.ArtifactRequestService;
import io.dataspaceconnector.services.messages.types.ContractAgreementService;
import io.dataspaceconnector.services.messages.types.ContractRequestService;
import io.dataspaceconnector.services.messages.types.DescriptionRequestService;
import io.dataspaceconnector.services.resources.ArtifactService;
import io.dataspaceconnector.services.resources.TemplateBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MessageServiceTest {

    @Autowired
    DescriptionRequestService descService;

    @Autowired
    ContractRequestService contractRequestService;

    @Autowired
    ContractAgreementService contractAgreementService;

    @Autowired
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
    ArtifactService artifactService;

    private final ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1616772571804L), ZoneOffset.UTC);

    @Test
    public void sendDescriptionRequestMessage_withoutRequestedElement_returnValidResponse() {
        */
/* ARRANGE *//*

        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var desc = new DescriptionRequestMessageDesc(recipient, null);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(descService.send(Mockito.eq(desc), Mockito.eq(""))).thenReturn(response);

        */
/* ACT *//*

        final var result = descService.sendMessage(recipient, null);

        */
/* ARRANGE *//*

        assertEquals(response, result);
    }

    @Test
    public void sendDescriptionRequestMessage_validRequestedElement_returnValidResponse() {
        */
/* ARRANGE *//*

        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var element = URI.create("https://requestedElement");
        final var desc = new DescriptionRequestMessageDesc(recipient, element);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(descService.send(Mockito.eq(desc), Mockito.eq(""))).thenReturn(response);

        */
/* ACT *//*

        final var result = descService.sendMessage(recipient, element);

        */
/* ARRANGE *//*

        assertEquals(response, result);
    }

    @Test
    public void validateDescriptionResponseMessage_validResponse_returnTrue() {
        */
/* ARRANGE *//*

        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(descService.isValidResponseType(Mockito.eq(response))).thenReturn(true);

        */
/* ACT *//*

        final var result = descService.validateResponse(response);

        */
/* ASSERT *//*

        assertTrue(result);
    }

    @Test
    public void validateDescriptionResponseMessage_invalidResponse_returnFalse() {
        */
/* ARRANGE *//*

        final var response = new HashMap<String, String>();
        response.put("header", "some invalid header");
        response.put("body", "some invalid body");

        Mockito.when(descService.isValidResponseType(Mockito.eq(response))).thenReturn(false);

        */
/* ACT *//*

        final var result = descService.validateResponse(response);

        */
/* ASSERT *//*

        assertFalse(result);
    }

    @Test
    public void sendContractRequestMessage_withValidContractRequest_returnValidResponse() {
        */
/* ARRANGE *//*

        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var contractId = URI.create("https://contractRequest");
        final var desc = new ContractRequestMessageDesc(recipient, contractId);
        final var request = new ContractRequestBuilder(contractId).build();
        final var requestAsRdf = request.toRdf();

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(contractRequestService.send(Mockito.eq(desc), Mockito.eq(requestAsRdf))).thenReturn(response);

        */
/* ACT *//*

        final var result = contractRequestService.sendMessage(recipient, request);

        */
/* ARRANGE *//*

        assertEquals(response, result);
    }

    @Test
    public void sendContractRequestMessage_withoutContractRequest_throwsIllegalArgumentException() {
        */
/* ARRANGE *//*

        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var contractId = URI.create("https://contractRequest");
        final var desc = new ContractRequestMessageDesc(recipient, contractId);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(contractRequestService.send(Mockito.eq(desc), Mockito.eq(null))).thenReturn(response);

        */
/* ACT & ARRANGE *//*

        assertThrows(IllegalArgumentException.class, () -> contractRequestService.sendMessage(recipient, null));
    }

    @Test
    public void validateContractRequestResponseMessage_validResponse_returnTrue() {
        */
/* ARRANGE *//*

        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(contractRequestService.isValidResponseType(Mockito.eq(response))).thenReturn(true);

        */
/* ACT *//*

        final var result = contractRequestService.validateResponse(response);

        */
/* ASSERT *//*

        assertTrue(result);
    }

    @Test
    public void validateContractRequestResponseMessage_invalidResponse_returnFalse() {
        */
/* ARRANGE *//*

        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(contractRequestService.isValidResponseType(Mockito.eq(response))).thenReturn(false);

        */
/* ACT *//*

        final var result = contractRequestService.validateResponse(response);

        */
/* ASSERT *//*

        assertFalse(result);
    }

    @Test
    public void sendContractAgreementMessage_withValidContractAgreement_returnValidResponse() {
        */
/* ARRANGE *//*

        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var contractId = URI.create("https://contractAgreement");
        final var desc = new ContractAgreementMessageDesc(recipient, contractId);
        final var agreement = new ContractAgreementBuilder(contractId)
                ._contractStart_(getDateAsXMLGregorianCalendar())
                .build();
        final var agreementAsRdf = agreement.toRdf();

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(contractAgreementService.send(Mockito.eq(desc), Mockito.eq(agreementAsRdf))).thenReturn(response);

        */
/* ACT *//*

        final var result = contractAgreementService.sendMessage(recipient, agreement);

        */
/* ASSERT *//*

        assertEquals(response, result);
    }

    @Test
    public void sendContractAgreementMessage_withoutContractAgreement_throwsIllegalArgumentException() {
        */
/* ARRANGE *//*

        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var contractId = URI.create("https://contractAgreement");
        final var desc = new ContractAgreementMessageDesc(recipient, contractId);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(contractAgreementService.send(Mockito.eq(desc), Mockito.eq(null))).thenReturn(response);

        */
/* ACT & ARRANGE *//*

        assertThrows(IllegalArgumentException.class, () -> contractAgreementService.sendMessage(recipient, null));
    }

    @Test
    public void validateContractAgreementResponseMessage_validResponse_returnTrue() {
        */
/* ARRANGE *//*

        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(contractAgreementService.isValidResponseType(Mockito.eq(response))).thenReturn(true);

        */
/* ACT *//*

        final var result = contractAgreementService.validateResponse(response);

        */
/* ASSERT *//*

        assertTrue(result);
    }

    @Test
    public void validateContractAgreementResponseMessage_invalidResponse_returnFalse() {
        */
/* ARRANGE *//*

        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(contractAgreementService.isValidResponseType(Mockito.eq(response))).thenReturn(false);

        */
/* ACT *//*

        final var result = contractAgreementService.validateResponse(response);

        */
/* ASSERT *//*

        assertFalse(result);
    }

    @Test
    public void sendArtifactRequestMessage_withValidInput_returnValidResponse() {
        */
/* ARRANGE *//*

        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var elementId = URI.create("https://element");
        final var agreementId = URI.create("https://agreement");
        final var desc = new ArtifactRequestMessageDesc(recipient, elementId, agreementId);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(artifactRequestService.send(Mockito.eq(desc), Mockito.eq(""))).thenReturn(response);

        */
/* ACT *//*

        final var result = artifactRequestService.sendMessage(recipient, elementId, agreementId);

        */
/* ARRANGE *//*

        assertEquals(response, result);
    }

    @Test
    public void validateArtifactResponseMessage_validResponse_returnTrue() {
        */
/* ARRANGE *//*

        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(artifactRequestService.isValidResponseType(Mockito.eq(response))).thenReturn(true);

        */
/* ACT *//*

        final var result = artifactRequestService.validateResponse(response);

        */
/* ASSERT *//*

        assertTrue(result);
    }

    @Test
    public void validateArtifactResponseMessage_invalidResponse_returnFalse() {
        */
/* ARRANGE *//*

        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(artifactRequestService.isValidResponseType(Mockito.eq(response))).thenReturn(false);

        */
/* ACT *//*

        final var result = artifactRequestService.validateResponse(response);

        */
/* ASSERT *//*

        assertFalse(result);
    }

    @SneakyThrows
    private XMLGregorianCalendar getDateAsXMLGregorianCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(Date.from(date.toInstant()));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }
}
*/
