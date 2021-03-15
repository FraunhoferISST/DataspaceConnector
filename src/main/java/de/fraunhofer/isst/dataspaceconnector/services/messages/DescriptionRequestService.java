package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionRequestDesc;
import org.springframework.stereotype.Service;

import java.net.URI;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids description request messages.
 */
@Service
public final class DescriptionRequestService extends MessageService<DescriptionRequestDesc> {

    @Override
    public Message buildMessage(final URI recipient, final DescriptionRequestDesc desc)
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
}
