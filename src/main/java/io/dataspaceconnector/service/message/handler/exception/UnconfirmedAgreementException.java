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
package io.dataspaceconnector.service.message.handler.exception;

import io.dataspaceconnector.model.agreement.Agreement;
import lombok.Getter;

/**
 * Thrown to indicate that an agreement could not be confirmed.
 */
public class UnconfirmedAgreementException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The agreement.
     */
    @Getter
    private final Agreement agreement;

    /**
     * Constructs an UnconfirmedAgreementException with the specified agreement and detail message.
     *
     * @param unconfirmed the agreement.
     * @param msg the detail message.
     */
    public UnconfirmedAgreementException(final Agreement unconfirmed, final String msg) {
        super(msg);
        this.agreement = unconfirmed;
    }

}
