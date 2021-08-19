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

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { ContractAgreementValidator.class, RuleListInputValidator.class})
public class IdsValidatorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @Mock
    private Response response;

    @MockBean
    private ContractManager contractManager;

    @Autowired
    private ContractAgreementValidator agreementValidator;

    @Autowired
    private RuleListInputValidator inputValidator;

    final XMLGregorianCalendar date = IdsMessageUtils.getGregorianNow();

    @Test
    @SneakyThrows
    public void contractAgreementValidator_agreementMatchesRequest_setAgreementAsProperty() {
        /* ARRANGE */
        final var request = getContractRequest();
        final var agreement = getContractAgreement();
        final var agreementString = agreement.toRdf();

        when(exchange.getProperty(ParameterUtils.CONTRACT_REQUEST_PARAM, ContractRequest.class))
                .thenReturn(request);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);
        when(response.getBody()).thenReturn(agreementString);
        when(contractManager.validateContractAgreement(agreementString, request))
                .thenReturn(agreement);
        doNothing().when(exchange).setProperty(anyString(), any());

        /* ACT */
        agreementValidator.process(exchange);

        /* ASSERT */
        verify(exchange, times(1))
                .setProperty(ParameterUtils.CONTRACT_AGREEMENT_PARAM, agreement);
    }

    @Test
    @SneakyThrows
    public void ruleListInputValidator_validRuleTarget_validationSucceeds() {
        /* ARRANGE */
        final var rule = getPermission();
        when(exchange.getProperty(ParameterUtils.RULE_LIST_PARAM, List.class))
                .thenReturn(Util.asList(rule));

        /* ACT */
        inputValidator.process(exchange);

        /* ASSERT */
        verify(exchange, never()).setProperty(anyString(), any());
        verify(exchange, never()).getIn();
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

}
