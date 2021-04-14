package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.ContractAgreementMessage;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {ContractAgreementService.class})
class ContractAgreementServiceTest {

    @MockBean
    private ConnectorService connectorService;

    @MockBean
    private IDSHttpService idsHttpService;

    @MockBean
    private DeserializationService deserializationService;

    @Autowired
    private ContractAgreementService agreementService;

    @Test
    public void buildMessage_null_throwsIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> agreementService.buildMessage(null));
    }

    @Test
    public void buildMessage_validDesc_returnValidMessage() {
        /* ARRANGE */
        final var desc = new ContractAgreementMessageDesc();
        desc.setRecipient(URI.create("https://recipient"));
        desc.setCorrelationMessage(URI.create("https://correlationMessage"));

        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(modelVersion);
        Mockito.when(connectorService.getCurrentDat()).thenReturn(token);

        /* ACT */
        final var result = (ContractAgreementMessage) agreementService.buildMessage(desc);

        /* ASSERT */
        assertEquals(1, result.getRecipientConnector().size());
        assertEquals(desc.getRecipient(), result.getRecipientConnector().get(0));
        assertEquals(desc.getCorrelationMessage(), result.getCorrelationMessage());
        assertEquals(connectorId, result.getIssuerConnector());
        assertEquals(modelVersion, result.getModelVersion());
        assertEquals(token, result.getSecurityToken());
    }
}
