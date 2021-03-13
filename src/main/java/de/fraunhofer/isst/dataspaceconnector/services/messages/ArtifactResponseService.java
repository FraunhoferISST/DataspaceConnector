package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.ArtifactResponseMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.services.ConfigurationService;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids artifact response messages.
 */
@Service
public final class ArtifactResponseService extends MessageService {

    /**
     * Class constructor with params.
     *
     * @param idsHttpService  The ids http service.
     * @param configContainer The configuration container.
     * @param configService   Service for the current connector configuration.
     */
    public ArtifactResponseService(@NonNull final IDSHttpService idsHttpService,
                                   @NonNull final ConfigurationContainer configContainer,
                                   @NonNull final ConfigurationService configService) {
        super(idsHttpService, configContainer, configService);
    }

    @Override
    public Message buildMessage(final URI recipient,
                                final List<URI> params) throws ConstraintViolationException {
        final var connectorId = getConfigService().getConnectorId();
        final var modelVersion = getConfigService().getConnectorOutboundModelVersion();
        final var token = getConfigService().getCurrentDat();

        final var contractId = params.get(0);
        final var correlationMessage = params.get(1);

        return new ArtifactResponseMessageBuilder()
                ._securityToken_(token)
                ._correlationMessage_(correlationMessage)
                ._issued_(getGregorianNow())
                ._issuerConnector_(connectorId)
                ._modelVersion_(modelVersion)
                ._senderAgent_(connectorId)
                ._recipientConnector_(Util.asList(recipient))
                ._transferContract_(contractId)
                .build();
    }
}
