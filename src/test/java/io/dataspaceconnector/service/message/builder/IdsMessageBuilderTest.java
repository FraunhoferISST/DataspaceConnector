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
package io.dataspaceconnector.service.message.builder;

import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ConnectorUnavailableMessage;
import de.fraunhofer.iais.eis.ConnectorUpdateMessage;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.iais.eis.RequestMessageBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.ResourceUnavailableMessage;
import de.fraunhofer.iais.eis.ResourceUpdateMessage;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.service.message.builder.type.SubscriptionRequestService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { ConnectorUnavailableMessageBuilder.class,
        ConnectorUpdateMessageBuilder.class, ResourceUnavailableMessageBuilder.class,
        ResourceUpdateMessageBuilder.class, SubscriptionRequestMessageBuilder.class})
public class IdsMessageBuilderTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private ConnectorService connectorService;

    @MockBean
    private SubscriptionRequestService subscriptionReqSvc;

    @Captor
    private ArgumentCaptor<Request<?, ?, ?>> captor;

    @Autowired
    private ConnectorUnavailableMessageBuilder connectorUnavailableBuilder;

    @Autowired
    private ConnectorUpdateMessageBuilder connectorUpdateBuilder;

    @Autowired
    private ResourceUnavailableMessageBuilder resourceUnavailableBuilder;

    @Autowired
    private ResourceUpdateMessageBuilder resourceUpdateBuilder;

    @Autowired
    private SubscriptionRequestMessageBuilder subscriptionRequestBuilder;

    private final URI recipient = URI.create("https://recipient.com");

    private final URI resourceId = URI.create("https://resource.com");

    private final BaseConnector connector = getConnector();

    private final Resource resource = getResource();

    @BeforeEach
    public void init() {
        final var token = getToken();

        when(connectorService.getOutboundModelVersion()).thenReturn("version");
        when(connectorService.getCurrentDat()).thenReturn(token);
        when(connectorService.getConnectorWithoutResources()).thenReturn(connector);
        when(connectorService.getConnectorId()).thenReturn(URI.create("https://connector.com"));

        when(exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class))
                .thenReturn(recipient);

        when(exchange.getIn()).thenReturn(in);
        doNothing().when(in).setBody(any());
    }

    @Test
    @SneakyThrows
    public void connectorUnavailableMessageBuilder_returnRequest() {
        /* ACT */
        connectorUnavailableBuilder.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setBody(captor.capture());

        final var request = captor.getValue();
        assertNotNull(request.getHeader());
        assertTrue(request.getHeader() instanceof ConnectorUnavailableMessage);
        assertNotNull(request.getBody());
        assertTrue(request.getBody() instanceof Connector);
    }

    @Test
    @SneakyThrows
    public void connectorUpdateMessageBuilder_returnRequest() {
        /* ACT */
        connectorUpdateBuilder.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setBody(captor.capture());

        final var request = captor.getValue();
        assertNotNull(request.getHeader());
        assertTrue(request.getHeader() instanceof ConnectorUpdateMessage);
        assertNotNull(request.getBody());
        assertTrue(request.getBody() instanceof Connector);
    }

    @Test
    @SneakyThrows
    public void resourceUnavailableMessageBuilder_returnRequest() {
        /* ARRANGE */
        when(exchange.getProperty(ParameterUtils.RESOURCE_ID_PARAM, URI.class))
                .thenReturn(resourceId);
        when(in.getBody(Resource.class)).thenReturn(resource);

        /* ACT */
        resourceUnavailableBuilder.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setBody(captor.capture());

        final var request = captor.getValue();
        assertNotNull(request.getHeader());
        assertTrue(request.getHeader() instanceof ResourceUnavailableMessage);
        assertNotNull(request.getBody());
        assertTrue(request.getBody() instanceof Resource);
    }

    @Test
    @SneakyThrows
    public void resourceUpdateMessageBuilder_returnRequest() {
        /* ARRANGE */
        when(exchange.getProperty(ParameterUtils.RESOURCE_ID_PARAM, URI.class))
                .thenReturn(resourceId);
        when(in.getBody(Resource.class)).thenReturn(resource);

        /* ACT */
        resourceUpdateBuilder.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setBody(captor.capture());

        final var request = captor.getValue();
        assertNotNull(request.getHeader());
        assertTrue(request.getHeader() instanceof ResourceUpdateMessage);
        assertNotNull(request.getBody());
        assertTrue(request.getBody() instanceof Resource);
    }

    @Test
    @SneakyThrows
    public void subscriptionRequestMessageBuilder_returnRequest() {
        /* ARRANGE */
        final var target = URI.create("https://target.com");
        final var subscription = new SubscriptionDesc();
        subscription.setTarget(target);
        final var idsMessage = getSubscriptionRequestMessage();

        when(exchange.getProperty(ParameterUtils.SUBSCRIPTION_DESC_PARAM, SubscriptionDesc.class))
                .thenReturn(subscription);
        when(subscriptionReqSvc.buildMessage(any())).thenReturn(idsMessage);

        /* ACT */
        subscriptionRequestBuilder.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setBody(captor.capture());

        final var request = captor.getValue();
        assertNotNull(request.getHeader());
        assertTrue(request.getHeader() instanceof RequestMessage);
        assertNotNull(request.getBody());
        assertTrue(request.getBody() instanceof SubscriptionDesc);
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private DynamicAttributeToken getToken() {
        return new DynamicAttributeTokenBuilder()
                ._tokenValue_("value")
                ._tokenFormat_(TokenFormat.JWT)
                .build();
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

    private Resource getResource() {
        return new ResourceBuilder().build();
    }

    private RequestMessage getSubscriptionRequestMessage() {
        return new RequestMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_("4.0.0")
                ._securityToken_(getToken())
                ._issuerConnector_(recipient)
                ._senderAgent_(recipient)
                .build();
    }

}
