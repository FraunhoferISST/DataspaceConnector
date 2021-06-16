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
package io.dataspaceconnector.usagecontrol;

import java.net.URI;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.ids.framework.util.IDSUtils;
import io.dataspaceconnector.config.ConnectorConfiguration;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.services.messages.types.LogMessageService;
import io.dataspaceconnector.services.messages.types.NotificationService;
import io.dataspaceconnector.common.RdfUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {PolicyExecutionService.class})
public class PolicyExecutionServiceTest {

    @MockBean
    private ConnectorConfiguration connectorConfig;

    @MockBean
    private ConnectorService connectorService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private LogMessageService logMessageService;

    @Autowired
    private PolicyExecutionService policyExecutionService;

    @Test
    public void sendAgreement_inputNull_doNothing() {
        /* ARRANGE */
        final var chUri= URI.create("https://clearing-house.com");

        when(connectorConfig.getClearingHouse()).thenReturn(chUri);

        /* ACT */
        policyExecutionService.sendAgreement(null);

        /* ASSERT */
        verify(logMessageService, never()).sendMessage(any(), any());
    }

    @Test
    public void sendAgreement_validInput_sendAgreementToClearingHouse() {
        /* ARRANGE */
        final var agreement = getContractAgreement();
        final var chUri= URI.create("https://clearing-house.com");

        when(connectorConfig.getClearingHouse()).thenReturn(chUri);
        doNothing().when(logMessageService).sendMessage(any(), any());

        /* ACT */
        policyExecutionService.sendAgreement(agreement);

        /* ASSERT */
        verify(logMessageService, times(1))
                .sendMessage(chUri, RdfUtils.toRdf(agreement));
    }

    @Test
    public void logDataAccess_clearingHouseUriPresent_sendLogMessageToClearingHouse() {
        /* ARRANGE */
        final var target = URI.create("https://target.com");
        final var chUri= URI.create("https://clearing-house.com");
        final var connectorId = URI.create("https://connector.com");

        when(connectorConfig.getClearingHouse()).thenReturn(chUri);
        doNothing().when(logMessageService).sendMessage(any(), any());
        when(connectorService.getConnectorId()).thenReturn(connectorId);

        /* ACT */
        policyExecutionService.logDataAccess(target);

        /* ASSERT */
        verify(logMessageService, times(1)).sendMessage(eq(chUri), any());
    }

    @Test
    public void reportDataAccess_sendNotificationMessage() {
        /* ARRANGE */
        final var notificationUri = "https://localhost:8080/api/ids/data";
        final var rule = getRule(notificationUri);
        final var target = URI.create("https://target.com");

        doNothing().when(notificationService).sendMessage(any(), any());

        /* ACT */
        policyExecutionService.reportDataAccess(rule, target);

        /* ASSERT */
        verify(notificationService, times(1))
                .sendMessage(eq(URI.create(notificationUri)), any());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private ContractAgreement getContractAgreement() {
        return new ContractAgreementBuilder(URI.create("https://agreement.com"))
                ._contractStart_(IDSUtils.getGregorianNow())
                ._contractEnd_(IDSUtils.getGregorianNow())
                .build();
    }

    private Rule getRule(final String endpoint) {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(
                        new TypedLiteral("usage-notification")))
                ._action_(Util.asList(Action.USE))
                ._postDuty_(Util.asList(new DutyBuilder()
                        ._action_(Util.asList(Action.NOTIFY))
                        ._constraint_(Util.asList(new ConstraintBuilder()
                                ._leftOperand_(LeftOperand.ENDPOINT)
                                ._operator_(BinaryOperator.DEFINES_AS)
                                ._rightOperand_(new RdfResource(endpoint, URI.create("xsd:anyURI")))
                                .build()))
                        .build()))
                .build();
    }

}
