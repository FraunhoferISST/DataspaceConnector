package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.ContractAgreementMessage;
import de.fraunhofer.iais.eis.ContractRequestMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.messages.AbstractMessageService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids contract request messages.
 */
@Service
public final class ContractRequestService extends AbstractMessageService<ContractRequestMessageDesc> {

    @Override
    public Message buildMessage(final URI recipient, final ContractRequestMessageDesc desc)
            throws ConstraintViolationException {
        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var contractId = desc.getTransferContract();

        return new ContractRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._transferContract_(contractId)
                .build();
    }

    /**
     * Checks if the response message is of the right type.
     *
     * @param message The received message response.
     * @return True if the response is valid, false if not.
     * @throws MessageResponseException If the header could not be extracted or deserialized.
     */
    public boolean validateResponse(final Map<String, String> message) throws MessageResponseException {
        final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
        final var idsMessage = getDeserializer().deserializeResponseMessage(header);

        return idsMessage instanceof ContractAgreementMessage;
    }
}
