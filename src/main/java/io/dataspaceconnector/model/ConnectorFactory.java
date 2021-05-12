package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Creates and updates a connector.
 */
@Component
public class ConnectorFactory implements AbstractFactory<Connector, ConnectorDesc> {

    /**
     * Default access url.
     */
    private static final URI DEFAULT_URI = URI.create("https://localhost:8080");

    /**
     * Default string value.
     */
    private static final String DEFAULT_STRING = "unknown";

    /**
     * @param desc The description of the entity.
     * @return The new connector entity.
     */
    @Override
    public Connector create(ConnectorDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.MESSAGE_NULL);

        final var connector = new Connector();

        update(connector, desc);

        return connector;
    }

    /**
     * @param connector The entity to be updated.
     * @param desc      The description of the new entity.
     * @return True, if connector is updated.
     */
    @Override
    public boolean update(Connector connector, ConnectorDesc desc) {
        Utils.requireNonNull(connector, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var newAccessUrl = updateAccessUrl(connector, connector.getAccessUrl());
        final var newTitle = updateTitle(connector, connector.getTitle());
        final var newStatus = updateRegisterStatus(connector, connector.getRegisterStatus());

        return newAccessUrl || newTitle || newStatus;
    }

    /**
     * @param connector The entity to be updated.
     * @param status    The registration status of the connector.
     * @return True, if connector is updated.
     */
    private boolean updateRegisterStatus(final Connector connector, final RegisterStatus status) {
        final boolean updated;
        if (connector.getRegisterStatus().equals(status)) {
            updated = false;
        } else {
            connector.setRegisterStatus(status);
            updated = true;
        }
        return updated;
    }

    /**
     * @param connector The entity to be updated.
     * @param title     The new title of the entity.
     * @return True, if connector is updated.
     */
    private boolean updateTitle(final Connector connector, final String title) {
        final var newTitle = MetadataUtils.updateString(connector.getTitle(), title,
                DEFAULT_STRING);
        newTitle.ifPresent(connector::setTitle);
        return newTitle.isPresent();
    }

    /**
     * @param connector The entity to be updated.
     * @param accessUrl The new access url of the entity.
     * @return True, if connector is updated.
     */
    private boolean updateAccessUrl(final Connector connector, final URI accessUrl) {
        final var newAccessUrl = MetadataUtils.updateUri(connector.getAccessUrl(), accessUrl,
                DEFAULT_URI);
        newAccessUrl.ifPresent(connector::setAccessUrl);
        return newAccessUrl.isPresent();
    }
}
