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

import de.fraunhofer.iais.eis.ContractRequest;
import lombok.Getter;

/**
 * Thrown to indicate that a contract request does not contain rules.
 */
public class MissingRulesException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The contract request.
     */
    @Getter
    private final transient ContractRequest contractRequest;

    /**
     * Constructs a MissingRulesException with the specified contract request and detail message.
     *
     * @param request the contract request.
     * @param msg the detail message.
     */
    public MissingRulesException(final ContractRequest request, final String msg) {
        super(msg);
        this.contractRequest = request;
    }

}
