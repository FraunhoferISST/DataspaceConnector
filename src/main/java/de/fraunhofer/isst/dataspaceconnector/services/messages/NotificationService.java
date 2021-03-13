package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.NotificationMessageBuilder;
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
 * Message service for ids notification messages.
 */
@Service
public final class NotificationService extends MessageService {

    /**
     * Class constructor with params.
     *
     * @param idsHttpService  The ids http service.
     * @param configContainer The configuration container.
     * @param configService   Service for the current connector configuration.
     */
    public NotificationService(@NonNull final IDSHttpService idsHttpService,
                               @NonNull final ConfigurationContainer configContainer,
                               @NonNull final ConfigurationService configService) {
        super(idsHttpService, configContainer, configService);
    }


    @Override
    public Message buildMessage(final URI recipient, final List<URI> params) throws ConstraintViolationException {
        final var connectorId = getConfigService().getConnectorId();
        final var modelVersion = getConfigService().getConnectorOutboundModelVersion();
        final var token = getConfigService().getCurrentDat();

        return new NotificationMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }
}
