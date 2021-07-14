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
package io.dataspaceconnector.service.message;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.Set;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.QueryLanguage;
import de.fraunhofer.iais.eis.QueryScope;
import de.fraunhofer.iais.eis.QueryTarget;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.protocol.multipart.MessageAndPayload;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import io.dataspaceconnector.service.configuration.BrokerService;
import io.dataspaceconnector.service.configuration.EntityLinkerService;
import io.dataspaceconnector.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service for sending ids messages.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class GlobalMessageService {

    /**
     * The service for communication with an ids broker.
     */
    private final @NotNull IDSBrokerService brokerSvc;

    /**
     * Service for relation between broker and offered resources.
     */
    private final @NotNull EntityLinkerService.BrokerOfferedResourcesLinker linker;

    /**
     * Service for the broker.
     */
    private final @NotNull BrokerService brokerService;


    /**
     * Send connector update message and validate received response.
     *
     * @param recipient The recipient.
     * @return True if the message was successfully processed by the recipient, false if not.
     * @throws MultipartParseException   If response could not be parsed to header and payload.
     * @throws ClaimsException           Exception that gets thrown, if errors occur while
     *                                   validating a DAT token.
     * @throws DapsTokenManagerException DAPS Token can not be acquired.
     * @throws IOException               Any other problems in establishing a connection
     *                                   to the target.
     */
    public boolean sendConnectorUpdateMessage(final URI recipient) throws
            MultipartParseException,
            ClaimsException,
            DapsTokenManagerException,
            IOException {
        final var response = brokerSvc.updateSelfDescriptionAtBroker(recipient);
        final var msg = String.format("Successfully registered connector. [url=(%s)]", recipient);
        final var result = validateResponse(response, msg);
        if (result) {
            updateBrokerRegistrationStatus(recipient);
        }
        return result;
    }

    /**
     * Send connector unavailable message and validate received response.
     * @param recipient The recipient.
     * @return True if the message was successfully processed by the recipient, false if not.
     * @throws MultipartParseException   If response could not be parsed to header and payload.
     * @throws ClaimsException           Exception that gets thrown, if errors occur while
     *                                   validating a DAT token.
     * @throws DapsTokenManagerException DAPS Token can not be acquired.
     * @throws IOException               Any other problems in establishing a connection
     *                                   to the target.
     */
    public boolean sendConnectorUnavailableMessage(final URI recipient) throws
            MultipartParseException,
            ClaimsException,
            DapsTokenManagerException,
            IOException {
        final var response = brokerSvc.unregisterAtBroker(recipient);
        final var msg = String.format("Successfully unregistered connector. [url=(%s)]", recipient);
        final var result = validateResponse(response, msg);
        if (result) {
            updateBrokerRegistrationStatus(recipient);
        }
        return result;
    }

    /**
     * Send resource update message and validate received response.
     * @param recipient The recipient.
     * @param resource  The ids resource that should be updated.
     * @return True if the message was successfully processed by the recipient, false if not.
     * @throws MultipartParseException   If response could not be parsed to header and payload.
     * @throws ClaimsException           Exception that gets thrown, if errors occur while
     *                                   validating a DAT token.
     * @throws DapsTokenManagerException DAPS Token can not be acquired.
     * @throws IOException               Any other problems in establishing a connection
     *                                   to the target.
     */
    public boolean sendResourceUpdateMessage(final URI recipient, final Resource resource) throws
            MultipartParseException,
            ClaimsException,
            DapsTokenManagerException,
            IOException {
        final var response = brokerSvc.updateResourceAtBroker(recipient, resource);
        final var msg = String.format("Successfully registered resource. "
                + "[resourceId=(%s), url=(%s)]", resource.getId(), recipient);
        return validateResponse(response, msg);
    }

    /**
     * Send resource unavailable message and validate received response.
     * @param recipient The recipient.
     * @param resource  The ids resource that should be updated.
     * @return True if the message was successfully processed by the recipient, false if not.
     * @throws MultipartParseException   If response could not be parsed to header and payload.
     * @throws ClaimsException           Exception that gets thrown, if errors occur while
     *                                   validating a DAT token.
     * @throws DapsTokenManagerException DAPS Token can not be acquired.
     * @throws IOException               Any other problems in establishing a connection
     *                                   to the target.
     */
    public boolean sendResourceUnavailableMessage(final URI recipient,
                                                  final Resource resource) throws
            MultipartParseException,
            ClaimsException,
            DapsTokenManagerException,
            IOException {
        final var response = brokerSvc.removeResourceFromBroker(recipient, resource);
        final var msg = String.format("Successfully unregistered resource. "
                + "[resourceId=(%s), url=(%s)]", resource.getId(), recipient);
        final var result = validateResponse(response, msg);
        if (result) {
            removeBrokerFromOfferedResourceBrokerList(recipient, resource);
        }
        return result;
    }

    /**
     * Send query message and validate received response.
     * @param recipient The recipient.
     * @param query     The query statement.
     * @return True if the message was successfully processed by the recipient, false if not.
     * @throws MultipartParseException   If response could not be parsed to header and payload.
     * @throws ClaimsException           Exception that gets thrown, if errors occur while
     *                                   validating a DAT token.
     * @throws DapsTokenManagerException DAPS Token can not be acquired.
     * @throws IOException               Any other problems in establishing a connection
     *                                   to the target.
     */
    public Optional<String> sendQueryMessage(final URI recipient, final String query) throws
            MultipartParseException,
            ClaimsException,
            DapsTokenManagerException,
            IOException {
        final var response = brokerSvc.queryBroker(recipient, query,
                QueryLanguage.SPARQL, QueryScope.ALL, QueryTarget.BROKER);
        final var msg = String.format("Successfully processed query. [url=(%s)]", recipient);
        if (validateResponse(response, msg)) {
            return response.getPayload();
        }

        return Optional.empty();
    }

    /**
     * Send query message and validate received response.
     * @param recipient The recipient.
     * @param term      The search term.
     * @param limit     The limit value.
     * @param offset    The offset value.
     * @return True if the message was successfully processed by the recipient, false if not.
     * @throws MultipartParseException   If response could not be parsed to header and payload.
     * @throws ClaimsException           Exception that gets thrown, if errors occur
     *                                   while validating a DAT token.
     * @throws DapsTokenManagerException DAPS Token can not be acquired.
     * @throws IOException               Any other problems in establishing a connection
     *                                   to the target.
     */
    public Optional<String> sendFullTextSearchQueryMessage(final URI recipient,
                                                           final String term,
                                                           final int limit,
                                                           final int offset) throws
            MultipartParseException,
            ClaimsException,
            DapsTokenManagerException,
            IOException {
        final var response = brokerSvc.fullTextSearchBroker(recipient, term,
                QueryScope.ALL, QueryTarget.BROKER, limit, offset);

        final var msg = String.format("Successfully processed full text search. [url=(%s)]",
                recipient);
        if (validateResponse(response, msg)) {
            return response.getPayload();
        }

        return Optional.empty();
    }

    /**
     * Check if a request was successfully processed by the recipient.
     * @param response The response map.
     * @param logMsg   The log message.
     * @return true if the recipient successfully processed the message, false otherwise.
     */
    private boolean validateResponse(final MessageAndPayload<? extends Message, ?> response,
                                     final String logMsg) {
        if (response != null) {
            if (log.isInfoEnabled()) {
                log.info("{}", logMsg);
            }
            return true;
        }

        return false;
    }

    /**
     * This method updates the registration status of the broker.
     * @param recipient The uri of the recipient.
     */
    private void updateBrokerRegistrationStatus(final URI recipient) {
        brokerService.updateRegistrationStatus(recipient);
    }

    /**
     * @param recipient The uri of the recipient.
     * @param resource  The offered resource.
     */
    private void removeBrokerFromOfferedResourceBrokerList(final URI recipient,
                                                           final Resource resource) {
        final var brokerId = brokerService.findByLocation(recipient);
        if (brokerId.isPresent()) {
            linker.remove(brokerId.get(), Set.of(UUIDUtils.uuidFromUri(resource.getId())));
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Removed Resource from Broker but Broker was not linked "
                        + "to resource in OfferedResource-Broker-List.");
            }
        }
    }
}
