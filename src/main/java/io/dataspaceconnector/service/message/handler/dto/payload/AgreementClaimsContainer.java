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
package io.dataspaceconnector.service.message.handler.dto.payload;

import de.fraunhofer.iais.eis.ContractAgreement;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Container used for publishing a new agreement together with the requesting connector's claims
 * as an event, as the claims are required for sending the agreement to the Clearing House.
 */
@Getter
@AllArgsConstructor
public class AgreementClaimsContainer {

    /**
     * The contract agreement.
     */
    private final ContractAgreement agreement;

    /**
     * The claims of the requesting connector.
     */
    private final Jws<Claims> claims;

}
