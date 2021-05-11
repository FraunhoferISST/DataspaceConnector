package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Creates and updates clearing houses.
 */
@Component
public class ClearingHouseFactory implements AbstractFactory<ClearingHouse, ClearingHouseDesc> {

    /**
     * Default access url.
     */
    private static final URI DEFAULT_URI = URI.create("https://clearinghouse.com");

    /**
     * Default string value.
     */
    private static final String DEFAULT_STRING = "unknown";

    /**
     * @param desc The description of the entity.
     * @return The new clearing house entity.
     */
    @Override
    public ClearingHouse create(ClearingHouseDesc desc) {
        return null;
    }

    @Override
    public boolean update(ClearingHouse clearingHouse, ClearingHouseDesc desc) {
        Utils.requireNonNull(clearingHouse, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var newAccessUrl = updateAccessUrl(clearingHouse, clearingHouse.getAccessUrl());
        final var newTitle = updateTitle(clearingHouse, clearingHouse.getTitle());
        final var newStatus = updateRegisterStatus(clearingHouse, clearingHouse.getRegisterStatus());

        return newAccessUrl || newTitle || newStatus;
    }

    /**
     * @param clearingHouse The entity to be updated.
     * @param status        The registration status of the clearing house.
     * @return True, if clearing house is updated.
     */
    private boolean updateRegisterStatus(ClearingHouse clearingHouse, RegisterStatus status) {
        final boolean updated;
        if (clearingHouse.getRegisterStatus().equals(status)) {
            updated = false;
        } else {
            clearingHouse.setRegisterStatus(status);
            updated = true;
        }
        return updated;
    }

    /**
     * @param clearingHouse The entity to be updated.
     * @param title         The new title of the entity.
     * @return True, if clearing house is updated
     */
    private boolean updateTitle(ClearingHouse clearingHouse, String title) {
        final var newTitle = MetadataUtils.updateString(clearingHouse.getTitle(), title,
                DEFAULT_STRING);
        newTitle.ifPresent(clearingHouse::setTitle);
        return newTitle.isPresent();
    }

    /**
     * @param clearingHouse The entity to be updated.
     * @param accessUrl     The new access url of the entity.
     * @return True, if clearing house is updated.
     */
    private boolean updateAccessUrl(ClearingHouse clearingHouse, URI accessUrl) {
        final var newAccessUrl = MetadataUtils.updateUri(clearingHouse.getAccessUrl(), accessUrl,
                DEFAULT_URI);
        newAccessUrl.ifPresent(clearingHouse::setAccessUrl);
        return newAccessUrl.isPresent();
    }
}
