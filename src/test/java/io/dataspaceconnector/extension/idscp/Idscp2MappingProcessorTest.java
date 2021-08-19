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
package io.dataspaceconnector.extension.idscp;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.extension.idscp.processor.ContractAgreementPreparer;
import io.dataspaceconnector.extension.idscp.processor.ContractRequestPreparer;
import io.dataspaceconnector.extension.idscp.processor.QueryPreparer;
import io.dataspaceconnector.extension.idscp.processor.RequestWithConnectorPayloadPreparer;
import io.dataspaceconnector.extension.idscp.processor.RequestWithResourcePayloadPreparer;
import io.dataspaceconnector.extension.idscp.processor.RequestWithoutPayloadPreparer;
import io.dataspaceconnector.service.message.handler.dto.Request;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { ContractAgreementPreparer.class, ContractRequestPreparer.class,
        QueryPreparer.class, RequestWithConnectorPayloadPreparer.class,
        RequestWithoutPayloadPreparer.class, RequestWithResourcePayloadPreparer.class})
public class Idscp2MappingProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @Autowired
    private ContractAgreementPreparer contractAgreementPreparer;

    @Autowired
    private ContractRequestPreparer contractRequestPreparer;

    @Autowired
    private QueryPreparer queryPreparer;

    @Autowired
    private RequestWithConnectorPayloadPreparer requestWithConnectorPayloadPreparer;

    @Autowired
    private RequestWithoutPayloadPreparer requestWithoutPayloadPreparer;

    @Autowired
    private RequestWithResourcePayloadPreparer requestWithResourcePayloadPreparer;

    final XMLGregorianCalendar date = IdsMessageUtils.getGregorianNow();

    final de.fraunhofer.iais.eis.Message message = getMessage();

    @BeforeEach
    public void init() {
        when(exchange.getIn()).thenReturn(in);
        doNothing().when(in).setHeader(anyString(), anyString());
        doNothing().when(in).setBody(any());
    }

    @Test
    @SneakyThrows
    public void contractAgreementPreparer_prepareIdscp2Message() {
        /* ARRANGE */
        final var agreement = getContractAgreement();
        final var request = new Request<>(message, agreement, null);
        when(in.getBody(Request.class)).thenReturn(request);

        /* ACT */
        contractAgreementPreparer.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setHeader(ParameterUtils.IDSCP_HEADER, message);
        verify(in, times(1))
                .setBody(agreement.toRdf().getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @SneakyThrows
    public void contractRequestPreparer_prepareIdscp2Message() {
        /* ARRANGE */
        final var contractRequest = getContractRequest();
        final var request = new Request<>(message, contractRequest, null);
        when(in.getBody(Request.class)).thenReturn(request);

        /* ACT */
        contractRequestPreparer.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setHeader(ParameterUtils.IDSCP_HEADER, message);
        verify(in, times(1))
                .setBody(contractRequest.toRdf().getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @SneakyThrows
    public void queryPreparer_prepareIdscp2Message() {
        /* ARRANGE */
        final var query = "some query string";
        final var request = new Request<>(message, query, null);
        when(in.getBody(Request.class)).thenReturn(request);

        /* ACT */
        queryPreparer.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setHeader(ParameterUtils.IDSCP_HEADER, message);
        verify(in, times(1))
                .setBody(query.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @SneakyThrows
    public void requestWithConnectorPayloadPreparer_prepareIdscp2Message() {
        /* ARRANGE */
        final var connector = getConnector();
        final var request = new Request<>(message, connector, null);
        when(in.getBody(Request.class)).thenReturn(request);

        /* ACT */
        requestWithConnectorPayloadPreparer.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setHeader(ParameterUtils.IDSCP_HEADER, message);
        verify(in, times(1))
                .setBody(connector.toRdf().getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @SneakyThrows
    public void requestWithoutPayloadPreparer_prepareIdscp2Message() {
        /* ARRANGE */
        final var request = new Request<>(message, null, null);
        when(in.getBody(Request.class)).thenReturn(request);

        /* ACT */
        requestWithoutPayloadPreparer.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setHeader(ParameterUtils.IDSCP_HEADER, message);
        verify(in, times(1)).setBody(null);
    }

    @Test
    @SneakyThrows
    public void requestWithResourcePayloadPreparer_prepareIdscp2Message() {
        /* ARRANGE */
        final var resource = getResource();
        final var request = new Request<>(message, resource, null);
        when(in.getBody(Request.class)).thenReturn(request);

        /* ACT */
        requestWithResourcePayloadPreparer.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setHeader(ParameterUtils.IDSCP_HEADER, message);
        verify(in, times(1))
                .setBody(resource.toRdf().getBytes(StandardCharsets.UTF_8));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private Permission getPermission() {
        return new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._target_(URI.create("https://some-artifact.com"))
                .build();
    }

    private ContractRequest getContractRequest() {
        return new ContractRequestBuilder()
                ._contractStart_(date)
                ._contractEnd_(date)
                ._permission_(Util.asList(getPermission()))
                .build();
    }

    private ContractAgreement getContractAgreement() {
        return new ContractAgreementBuilder()
                ._contractStart_(date)
                ._contractEnd_(date)
                ._permission_(Util.asList(getPermission()))
                .build();
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

    private Resource getResource() {
        return new ResourceBuilder().build();
    }

    private BaseConnector getConnector() {
        return new BaseConnectorBuilder()
                ._curator_(URI.create("https://connector.com"))
                ._outboundModelVersion_("version")
                ._maintainer_(URI.create("https://connector.com"))
                ._inboundModelVersion_(new ArrayList<>(Arrays.asList("version")))
                ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                ._hasDefaultEndpoint_(new ConnectorEndpointBuilder()
                        ._accessURL_(URI.create("https://url.com"))
                        .build())
                .build();
    }

}
