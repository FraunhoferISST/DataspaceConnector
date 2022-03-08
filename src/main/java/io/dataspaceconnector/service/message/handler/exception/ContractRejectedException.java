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

import lombok.Getter;

import java.net.URI;

/**
 * Thrown to indicate that a contract request was rejected.
 */
public class ContractRejectedException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The issuer of the contract request.
     */
    @Getter
    private final URI issuerConnector;

    /**
     * ID of the ContractRequestMessage.
     */
    @Getter
    private final URI messageId;

    /**
     * Constructs a ContractRejectedException with the specified request issuer, ID of the request
     * message and detail message.
     *
     * @param issuer the issuer connector.
     * @param message the request message id.
     * @param msg the detail message.
     */
    public ContractRejectedException(final URI issuer, final URI message, final String msg) {
        super(msg);
        this.issuerConnector = issuer;
        this.messageId = message;
    }

}
