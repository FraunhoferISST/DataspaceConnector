package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.ContractRejectionMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractRejectionMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.messages.AbstractMessageService;
import org.springframework.stereotype.Service;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids contract request messages.
 */
@Service
public final class ContractRejectionService extends AbstractMessageService<ContractRejectionMessageDesc> {

    @Override
    public Message buildMessage(final ContractRejectionMessageDesc desc)
            throws ConstraintViolationException {
        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var recipient = desc.getRecipient();
        final var correlationMessage = desc.getCorrelationMessage();
        final var rejectionReason = new TypedLiteral("Contract not accepted.", "en");

        return new ContractRejectionMessageBuilder()
                ._securityToken_(token)
                ._correlationMessage_(correlationMessage)
                ._issued_(getGregorianNow())
                ._issuerConnector_(connectorId)
                ._modelVersion_(modelVersion)
                ._senderAgent_(connectorId)
                ._recipientConnector_(Util.asList(recipient))
                ._rejectionReason_(RejectionReason.MALFORMED_MESSAGE)
                ._contractRejectionReason_(rejectionReason)
                .build();
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return null;
    }
}
