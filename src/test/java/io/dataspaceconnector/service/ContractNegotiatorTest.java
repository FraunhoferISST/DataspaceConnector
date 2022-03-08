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
package io.dataspaceconnector.service;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.message.builder.type.ContractAgreementService;
import io.dataspaceconnector.service.message.builder.type.ContractRequestService;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = { ContractNegotiator.class })
class ContractNegotiatorTest {

    @MockBean
    private ContractRequestService contractReqSvc;

    @MockBean
    private ContractManager contractManager;

    @MockBean
    private ContractAgreementService agreementSvc;

    @MockBean
    private EntityPersistenceService persistenceSvc;

    @Autowired
    private ContractNegotiator negotiator;

    @Test
    @SuppressWarnings("unchecked")
    public void negotiate_validInput_producesAgreement()
            throws DatatypeConfigurationException, UnexpectedResponseException {
        /* ARRANGE */
        final var ruleList = (List<Rule>) (List<?>) Arrays.asList(new PermissionBuilder()
                ._action_(List.of(Action.NOTIFY))
                .build());
        final var request = new ContractRequestBuilder()
                ._contractStart_(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar("2009-05-07T17:05:45.678Z"))
                .build();
        final var recipient = URI.create("https://recipient");
        Mockito.when(contractManager.buildContractRequest(eq(ruleList))).thenReturn(request);

        final var response = new HashMap<String, String>();
        response.put("payload", "Bye");
        Mockito.when(contractReqSvc.sendMessage(eq(recipient), eq(request))).thenReturn(response);

        final var agreement = new ContractAgreementBuilder()
                ._contractStart_(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar("2009-05-07T17:05:45.678Z"))
                .build();
        Mockito.when(contractManager.validateContractAgreement(eq(response.get("payload")), eq(request))).thenReturn(agreement);

        final var agreementId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        Mockito.when(persistenceSvc.saveContractAgreement(eq(agreement))).thenReturn(agreementId);

        /* ACT */
        final var result = negotiator.negotiate(recipient, ruleList);

        /* ASSERT */
        Mockito.verify(agreementSvc, Mockito.atLeastOnce()).sendMessage(eq(recipient), eq(agreement));
        assertEquals(agreementId, result);
    }
}
