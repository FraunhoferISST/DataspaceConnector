package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a contract.
 */
@Component
public class AgreementFactory implements AbstractFactory<Agreement, AgreementDesc> {

    /**
     * The default remote id.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * The default value.
     */
    static final String DEFAULT_VALUE = "";

    /**
     * Default constructor.
     */
    public AgreementFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new contract.
     * @param desc The description of the new contract.
     * @return The new contract.
     * @throws IllegalArgumentException if the description is null.
     */
    @Override
    public Agreement create(final AgreementDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var agreement = new Agreement();
        agreement.setArtifacts(new ArrayList<>());

        update(agreement, desc);

        return agreement;
    }

    /**
     * Update a contract.
     * @param agreement The contract to be updated.
     * @param desc      The new contract description.
     * @return True if the contract has been modified.
     * @throws IllegalArgumentException if any of the passed arguments is null.
     */
    @Override
    public boolean update(final Agreement agreement, final AgreementDesc desc) {
        Utils.requireNonNull(agreement, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedRemoteId = this.updateRemoteId(agreement, desc.getRemoteId());
        final var hasUpdatedConfirmed = this.updateHasConfirmed(agreement, desc.isConfirmed());
        final var hasUpdatedValue = this.updateValue(agreement, desc.getValue());
        final var hasUpdatedAdditional = this.updateAdditional(agreement, desc.getAdditional());

        return hasUpdatedRemoteId || hasUpdatedConfirmed || hasUpdatedValue || hasUpdatedAdditional;
    }

    private boolean updateRemoteId(final Agreement agreement, final URI remoteId) {
        final var newUri =
                MetadataUtils.updateUri(agreement.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(agreement::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateHasConfirmed(final Agreement agreement, final boolean confirmed) {
        if (agreement.isConfirmed() != confirmed) {
            agreement.setConfirmed(confirmed);
            return true;
        }

        return false;
    }

    private boolean updateValue(final Agreement agreement, final String value) {
        final var newValue = MetadataUtils.updateString(agreement.getValue(), value, DEFAULT_VALUE);
        newValue.ifPresent(agreement::setValue);

        return newValue.isPresent();
    }

    private boolean updateAdditional(
            final Agreement agreement, final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                agreement.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(agreement::setAdditional);

        return newAdditional.isPresent();
    }
}
