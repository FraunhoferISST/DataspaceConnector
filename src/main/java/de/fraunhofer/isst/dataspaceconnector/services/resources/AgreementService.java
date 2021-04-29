package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import de.fraunhofer.isst.dataspaceconnector.repositories.AgreementRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;

/**
 * Handles the basic logic for contracts.
 */
@Service
@NoArgsConstructor
public class AgreementService extends BaseEntityService<Agreement, AgreementDesc> {

    /**
     * Compares the agreement with the persisted one. If they are equal the agreement
     * will be confirmed.
     * @param agreement The agreement that should be confirmed.
     * @return true - if the was unconfirmed and has been changed to confirmed.
     * @throws ResourceNotFoundException if the agreement does no longer exist.
     */
    public boolean confirmAgreement(final Agreement agreement) {
        final var persisted = this.get(agreement.getId());
        var isConfirmed = false;
        if (persisted.equals(agreement)) {
            final var repo = (AgreementRepository) getRepository();
            repo.confirmAgreement(agreement.getId());
            isConfirmed = true;
        }

        return isConfirmed;
    }
}
