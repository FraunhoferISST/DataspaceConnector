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
package io.dataspaceconnector.controller.message.ids.validator;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.exception.InvalidResponseException;
import io.dataspaceconnector.service.message.builder.type.ArtifactRequestService;
import io.dataspaceconnector.service.message.builder.type.ContractAgreementService;
import io.dataspaceconnector.service.message.builder.type.ContractRequestService;
import io.dataspaceconnector.service.message.builder.type.DescriptionRequestService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { ArtifactResponseValidator.class,
        ContractAgreementResponseValidator.class, ContractResponseValidator.class,
        DescriptionResponseValidator.class})
public class IdsResponseMessageValidatorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private ArtifactRequestService artifactRequestService;

    @MockBean
    private ContractAgreementService contractAgreementService;

    @MockBean
    private ContractRequestService contractRequestService;

    @MockBean
    private DescriptionRequestService descriptionRequestService;

    @Autowired
    private ArtifactResponseValidator artifactResponseValidator;

    @Autowired
    private  ContractAgreementResponseValidator contractAgreementResponseValidator;

    @Autowired
    private ContractResponseValidator contractResponseValidator;

    @Autowired
    private DescriptionResponseValidator descriptionResponseValidator;

    final XMLGregorianCalendar date = IdsMessageUtils.getGregorianNow();

    @Test
    public void artifactResponseValidator_validationFails_throwInvalidResponseException() {
        /* ARRANGE */
        final var response = getResponse();
        when(artifactRequestService.validateResponse(any())).thenReturn(false);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);

        /* ACT && ASSERT */
        assertThrows(InvalidResponseException.class,
                () -> artifactResponseValidator.process(exchange));
    }

    @Test
    public void contractAgreementResponseValidator_validationFails_throwInvalidResponseException() {
        /* ARRANGE */
        final var response = getResponse();
        when(contractAgreementService.validateResponse(any())).thenReturn(false);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);

        /* ACT && ASSERT */
        assertThrows(InvalidResponseException.class,
                () -> contractAgreementResponseValidator.process(exchange));
    }

    @Test
    public void contractResponseValidator_validationFails_throwInvalidResponseException() {
        /* ARRANGE */
        final var response = getResponse();
        when(contractRequestService.validateResponse(any())).thenReturn(false);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);

        /* ACT && ASSERT */
        assertThrows(InvalidResponseException.class,
                () -> contractResponseValidator.process(exchange));
    }

    @Test
    public void descriptionResponseValidator_validationFails_throwInvalidResponseException() {
        /* ARRANGE */
        final var response = getResponse();
        when(descriptionRequestService.validateResponse(any())).thenReturn(false);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);

        /* ACT && ASSERT */
        assertThrows(InvalidResponseException.class,
                () -> descriptionResponseValidator.process(exchange));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private Response getResponse() {
        return new Response(getMessage(), "body");
    }

    private DescriptionRequestMessage getMessage() {
        return new DescriptionRequestMessageBuilder()
                ._issuerConnector_(URI.create("https://connector.com"))
                ._issued_(date)
                ._securityToken_(new DynamicAttributeTokenBuilder()
                        ._tokenValue_("value")
                        ._tokenFormat_(TokenFormat.JWT)
                        .build())
                ._modelVersion_("version")
                ._senderAgent_(URI.create("https://connector.com"))
                .build();
    }

}
