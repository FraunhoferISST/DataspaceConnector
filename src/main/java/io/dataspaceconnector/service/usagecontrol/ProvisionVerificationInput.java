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
package io.dataspaceconnector.service.usagecontrol;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.SecurityProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Optional;

/**
 * A DTO for information required to decide if data provision should be allowed.
 */
@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class ProvisionVerificationInput {

    /**
     * The id of the targeted artifact.
     */
    private URI target;

    /**
     * The id of the issuing connector.
     */
    private URI issuerConnector;

    /**
     * The contract agreements for policy verification.
     */
    private ContractAgreement agreement;

    /**
     * The security profile.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<SecurityProfile> securityProfile;
}
