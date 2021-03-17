package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.DescriptionResponseMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionResponseMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.messages.AbstractMessageService;
import org.springframework.stereotype.Service;

import java.net.URI;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids description response messages.
 */
@Service
public final class DescriptionResponseService extends AbstractMessageService<DescriptionResponseMessageDesc> {

    @Override
    public Message buildMessage(final URI recipient, final DescriptionResponseMessageDesc desc)
            throws ConstraintViolationException {
        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var correlationMessage = desc.getCorrelationMessage();

        return new DescriptionResponseMessageBuilder()
                ._securityToken_(token)
                ._correlationMessage_(correlationMessage)
                ._issued_(getGregorianNow())
                ._issuerConnector_(connectorId)
                ._modelVersion_(modelVersion)
                ._senderAgent_(connectorId)
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }
}
