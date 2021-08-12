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
package io.dataspaceconnector.controller.message.ids.processor;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.service.EntityPersistenceService;
import io.dataspaceconnector.service.EntityUpdateService;
import io.dataspaceconnector.service.message.handler.dto.Response;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { AgreementToArtifactsLinker.class,
        ContractAgreementPersistenceProcessor.class, DataPersistenceProcessor.class,
        MetadataPersistenceProcessor.class})
public class IdsResponseProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private EntityPersistenceService persistenceService;

    @MockBean
    private EntityUpdateService updateService;

    @Autowired
    private AgreementToArtifactsLinker linker;

    @Autowired
    private ContractAgreementPersistenceProcessor agreementPersistenceProcessor;

    @Autowired
    private DataPersistenceProcessor dataPersistenceProcessor;

    @Autowired
    private MetadataPersistenceProcessor metadataPersistenceProcessor;

    final XMLGregorianCalendar date = IdsMessageUtils.getGregorianNow();

    @Test
    @SneakyThrows
    public void agreementToArtifactsLinker_callUpdateService() {
        /* ARRANGE */
        final var uuid = UUID.randomUUID();
        final var uri = URI.create("https://artifact.com");
        final var list = Util.asList(uri);

        when(exchange.getProperty(ParameterUtils.AGREEMENT_ID_PARAM, UUID.class))
                .thenReturn(uuid);
        when(exchange.getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class))
                .thenReturn(list);
        doNothing().when(updateService).linkArtifactToAgreement(list, uuid);

        /* ACT */
        linker.process(exchange);

        /* ASSERT */
        verify(updateService, times(1)).linkArtifactToAgreement(list, uuid);
    }

    @Test
    @SneakyThrows
    public void contractAgreementPersistenceProcessor_allPersistenceService() {
        /* ARRANGE */
        final var agreement = getContractAgreement();
        final var uuid = UUID.randomUUID();

        when(exchange.getProperty(ParameterUtils.CONTRACT_AGREEMENT_PARAM, ContractAgreement.class))
                .thenReturn(agreement);
        when(persistenceService.saveContractAgreement(agreement)).thenReturn(uuid);
        doNothing().when(exchange).setProperty(anyString(), any());

        /* ACT */
        agreementPersistenceProcessor.process(exchange);

        /* ASSERT */
        verify(exchange, times(1))
                .setProperty(ParameterUtils.AGREEMENT_ID_PARAM, uuid);
    }

    @Test
    @SneakyThrows
    public void dataPersistenceProcessor_callPersistenceService() {
        /* ARRANGE */
        final var message = getMessage();
        final var response = new Response(message, "body");
        final var uri = URI.create("https://artifact.com");

        when(exchange.getProperty(Exchange.LOOP_INDEX, Integer.class))
                .thenReturn(Integer.valueOf(0));
        when(exchange.getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class))
                .thenReturn(Util.asList(uri));
        doNothing().when(persistenceService).saveData(any(), any());
        doNothing().when(exchange).setProperty(anyString(), any());
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);

        /* ACT */
        dataPersistenceProcessor.process(exchange);

        /* ASSERT */
        verify(exchange, times(1))
                .setProperty(ParameterUtils.CURRENT_ARTIFACT_PARAM, uri);
        verify(persistenceService, times(1)).saveData(any(), eq((uri)));
    }

    @Test
    @SneakyThrows
    public void metadataPersistenceProcessor_callPersistenceService() {
        /* ARRANGE */
        final var message = getMessage();
        final var response = new Response(message, "body");
        final var uri = URI.create("https://artifact.com");
        final var list = Util.asList(uri);
        final var recipient = URI.create("https://recipient.com");
        final var download = true;

        when(exchange.getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class))
                .thenReturn(list);
        when(exchange.getProperty(ParameterUtils.DOWNLOAD_PARAM, boolean.class))
                .thenReturn(download);
        when(exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class))
                .thenReturn(recipient);

        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);

        /* ACT */
        metadataPersistenceProcessor.process(exchange);

        /* ASSERT */
        verify(persistenceService, times(1)).saveMetadata(any(), eq(list),
                eq(download), eq(recipient));
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

}
