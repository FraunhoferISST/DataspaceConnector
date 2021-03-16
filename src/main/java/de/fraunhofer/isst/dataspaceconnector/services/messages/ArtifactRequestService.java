package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ArtifactRequestMessageDesc;
import org.springframework.stereotype.Service;

import java.net.URI;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids artifact request messages.
 */
@Service
public final class ArtifactRequestService extends MessageService<ArtifactRequestMessageDesc> {

    @Override
    public Message buildMessage(final URI recipient, final ArtifactRequestMessageDesc desc)
            throws ConstraintViolationException {
        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var artifactId = desc.getRequestedArtifact();
        final var contractId = desc.getTransferContract();

        return new ArtifactRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._requestedArtifact_(artifactId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._transferContract_(contractId)
                .build();
    }
}
