package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnexpectedMessageType;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.messages.AbstractMessageService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids description request messages.
 */
@Service
public final class DescriptionRequestService extends AbstractMessageService<DescriptionRequestMessageDesc> {

    @Override
    public Message buildMessage(final URI recipient, final DescriptionRequestMessageDesc desc)
            throws ConstraintViolationException {
        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var elementId = desc.getRequestedElement();

        return new DescriptionRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._requestedElement_(elementId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }

    /**
     * Checks if the response message is of the right type.
     *
     * @param message The received message response.
     * @throws MessageResponseException If the header could not be extracted or deserialized.
     */
    public void validateResponse(final Map<String, String> message) throws MessageResponseException, UnexpectedMessageType {
        final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
        final var idsMessage = getDeserializer().deserializeResponseMessage(header);

        final var validType = idsMessage instanceof DescriptionResponseMessage;
        if (!validType) {
            throw new UnexpectedMessageType("Unexpected type.");
        }
    }
}
