package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.ContractAgreementMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractAgreementDesc;
import org.springframework.stereotype.Service;

import java.net.URI;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids contract request messages.
 */
@Service
public final class ContractAgreementService extends MessageService<ContractAgreementDesc> {

    @Override
    public Message buildMessage(final URI recipient, final ContractAgreementDesc desc)
            throws ConstraintViolationException {
        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var correlationMessage = desc.getCorrelationMessage();

        return new ContractAgreementMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._correlationMessage_(correlationMessage)
                .build();
    }
}
