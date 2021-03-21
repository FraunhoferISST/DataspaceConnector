package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a contract.
 */
@Component
public class AgreementFactory implements AbstractFactory<Agreement, AgreementDesc> {

    /**
     * Default constructor.
     */
    public AgreementFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new contract.
     *
     * @param desc The description of the new contract.
     * @return The new contract.
     */
    @Override
    public Agreement create(final AgreementDesc desc) {
        final var agreement = new Agreement();
        update(agreement, desc);

        return agreement;
    }

    /**
     * Update a contract.
     *
     * @param agreement The contract to be updated.
     * @param desc     The new contract description.
     * @return True if the contract has been modified.
     */
    @Override
    public boolean update(final Agreement agreement, final AgreementDesc desc) {
        return this.updateValue(agreement, desc.getValue());
    }

    private boolean updateValue(final Agreement agreement, final String value) {
        final var newValue = MetadataUtils.updateString(agreement.getValue(), value, "");
        newValue.ifPresent(agreement::setValue);

        return newValue.isPresent();
    }
}
