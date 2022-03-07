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
package io.dataspaceconnector.service.message.handler.event;

import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.service.message.handler.dto.payload.AgreementClaimsContainer;
import io.dataspaceconnector.service.usagecontrol.PolicyExecutionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = {AgreementEventHandler.class})
class AgreementEventHandlerTest {

    @Mock
    private Jws<Claims> claims;

    @MockBean
    private PolicyExecutionService executionService;

    @Autowired
    private AgreementEventHandler eventHandler;

    @Test
    void handleAgreementEvent_willCallExecutionService() {
        /* ARRANGE */
        final var agreement = new ContractAgreementBuilder()
                ._contractStart_(IdsMessageUtils.getGregorianNow())
                .build();

        final var agreementContainer = new AgreementClaimsContainer(agreement, claims);

        /* ACT */
        eventHandler.handleAgreementEvent(agreementContainer);

        /* ASSERT */
        Mockito.verify(executionService, Mockito.atLeastOnce())
                .sendAgreement(eq(agreement), eq(claims));
    }
}
