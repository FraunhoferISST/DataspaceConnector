package io.dataspaceconnector.camel.event;

import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.service.usagecontrol.PolicyExecutionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = {AgreementEventHandler.class})
class AgreementEventHandlerTest {

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

        /* ACT */
        eventHandler.handleAgreementEvent(agreement);

        /* ASSERT */
        Mockito.verify(executionService, Mockito.atLeastOnce()).sendAgreement(eq(agreement));
    }
}
