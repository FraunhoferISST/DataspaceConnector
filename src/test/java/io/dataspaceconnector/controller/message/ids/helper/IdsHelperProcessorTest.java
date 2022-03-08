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
package io.dataspaceconnector.controller.message.ids.helper;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.service.message.handler.exception.InvalidResponseException;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { ConfigurationUpdater.class, PolicyRestrictionProcessor.class,
        ResourceFinder.class})
public class IdsHelperProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private ConnectorService connectorService;

    @Autowired
    private ConfigurationUpdater configurationUpdater;

    @Autowired
    private PolicyRestrictionProcessor restrictionProcessor;

    @Autowired
    private ResourceFinder resourceFinder;

    @Test
    @SneakyThrows
    public void configurationUpdater_callConnectorService() {
        /* ARRANGE */
        doNothing().when(connectorService).updateConfigModel();

        /* ACT */
        configurationUpdater.process(exchange);

        /* ASSERT */
        verify(connectorService, times(1)).updateConfigModel();
    }

    @Test
    public void policyRestrictionProcessor_throwPolicyRestrictionException() {
        /* ARRANGE */
        final var exception = new InvalidResponseException(new HashMap<>(), "invalid");

        when(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class))
                .thenReturn(exception);

        /* ACT && ASSERT */
        assertThrows(PolicyRestrictionException.class,
                () -> restrictionProcessor.process(exchange));
    }

    @Test
    @SneakyThrows
    public void resourceFinder_setResourceAsExchangeMessageBody() {
        /* ARRANGE */
        final var resource = getResource();
        final var uri = resource.getId();

        when(exchange.getProperty(ParameterUtils.RESOURCE_ID_PARAM, URI.class))
                .thenReturn(uri);
        when(connectorService.getOfferedResourceById(uri)).thenReturn(Optional.of(resource));

        when(exchange.getIn()).thenReturn(in);
        doNothing().when(in).setBody(any());

        /* ACT */
        resourceFinder.process(exchange);

        /* ASSERT */
        verify(in, times(1)).setBody(resource);
    }

    @Test
    @SneakyThrows
    public void resourceFinder_resourceNotFound_throwResourceNotFoundException() {
        /* ARRANGE */
        final var uri = URI.create("https://resource.com");

        when(exchange.getProperty(ParameterUtils.RESOURCE_ID_PARAM, URI.class))
                .thenReturn(uri);
        when(connectorService.getOfferedResourceById(uri)).thenReturn(Optional.empty());

        /* ACT */
        assertThrows(ResourceNotFoundException.class, () -> resourceFinder.process(exchange));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private Resource getResource() {
        return new ResourceBuilder().build();
    }

}
