/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.agreement.AgreementDesc;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.repository.AgreementRepository;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.service.resource.base.BaseEntityService;

/**
 * Handles the basic logic for contracts.
 */
public class AgreementService extends BaseEntityService<Agreement, AgreementDesc> {

    /**
     * Constructor.
     *
     * @param repository The underlying agreement repo.
     * @param factory    The factory for the agreement logic.
     */
    public AgreementService(
            final BaseEntityRepository<Agreement> repository,
            final AbstractFactory<Agreement, AgreementDesc> factory) {
        super(repository, factory);
    }

    /**
     * Compares the agreement with the persisted one. If they are equal the agreement will be
     * confirmed.
     *
     * @param agreement The agreement that should be confirmed.
     * @return true - if the was unconfirmed and has been changed to confirmed.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException if the agreement
     *                                                                          does no longer
     *                                                                          exist.
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
