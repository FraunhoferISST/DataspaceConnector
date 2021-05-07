package io.dataspaceconnector.services.resources;

import io.dataspaceconnector.model.Agreement;
import io.dataspaceconnector.model.AgreementDesc;
import io.dataspaceconnector.repositories.AgreementRepository;
import io.dataspaceconnector.exceptions.ResourceNotFoundException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

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
