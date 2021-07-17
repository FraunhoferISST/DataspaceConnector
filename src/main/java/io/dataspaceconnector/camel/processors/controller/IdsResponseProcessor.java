/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.camel.processors.controller;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.fraunhofer.iais.eis.ContractAgreement;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.service.EntityPersistenceService;
import io.dataspaceconnector.service.EntityUpdateService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Superclass for all processors that process a response received after sending a request.
 */
public abstract class IdsResponseProcessor implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method with the {@link Exchange}.
     *
     * @param exchange the exchange.
     * @throws Exception if building the message or payload fails.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange);
    }

    /**
     * Processes the response. To be implemented by sub classes.
     *
     * @param exchange the exchange.
     * @throws Exception if processing the response fails.
     */
    protected abstract void processInternal(Exchange exchange) throws Exception;

}

/**
 * Persists a contract agreement received as the response to a ContractRequestMessage.
 */
@Component("ContractAgreementPersistenceProcessor")
@RequiredArgsConstructor
class ContractAgreementPersistenceProcessor extends IdsResponseProcessor {

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Persists the contract agreement.
     *
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var agreement = exchange.getProperty("contractAgreement", ContractAgreement.class);
        final var agreementId = persistenceSvc.saveContractAgreement(agreement);
        exchange.setProperty("agreementId", agreementId);
    }

}

/**
 * Persists the metadata received as the response to a DescriptionRequestMessage.
 */
@Component("MetadataPersistenceProcessor")
@RequiredArgsConstructor
class MetadataPersistenceProcessor extends IdsResponseProcessor {

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Persists the metadata.
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var response = exchange.getIn().getBody(Response.class);
        final var map = new HashMap<String, String>();
        map.put("header", response.getHeader().toRdf());
        map.put("payload", response.getBody());

        final var artifacts = (List<URI>) exchange.getProperty("artifacts", List.class);
        final var download = exchange.getProperty("download", boolean.class);
        final var recipient = exchange.getProperty("recipient", URI.class);

        persistenceSvc.saveMetadata(map, artifacts, download, recipient);
    }

}

/**
 * Persists the data received as the response to an ArtifactRequestMessage.
 */
@Component("DataPersistenceProcessor")
@RequiredArgsConstructor
class DataPersistenceProcessor extends IdsResponseProcessor {

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Persists the data.
     *
     * @param exchange the exchange.
     * @throws IOException if persisting the data fails.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws IOException {
        final var response = exchange.getIn().getBody(Response.class);
        final var map = new HashMap<String, String>();
        map.put("header", response.getHeader().toRdf());
        map.put("payload", response.getBody());

        final var artifactId = exchange.getProperty(Exchange.LOOP_INDEX, URI.class);

        // Set current artifact as exchange property so it is available for error handling.
        exchange.setProperty("currentArtifact", artifactId);

        persistenceSvc.saveData(map, artifactId);
    }

}

/**
 * Links the contract agreement received as the response to a ContractRequestMessage to the
 * artifacts created from the metadata received as the response to a DescriptionRequestMessage.
 */
@Component("AgreementToArtifactsLinker")
@RequiredArgsConstructor
class AgreementToArtifactsLinker extends IdsResponseProcessor {

    /**
     * Service for updating database entities.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Links the contract agreement to the artifacts.
     *
     * @param exchange the exchange.
     * @throws Exception if linking the agreement to the artifacts fails.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws Exception {
        final var agreementId = exchange.getProperty("agreementId", UUID.class);
        final var artifacts = (List<URI>) exchange.getProperty("artifacts", List.class);

        updateService.linkArtifactToAgreement(artifacts, agreementId);
    }

}
