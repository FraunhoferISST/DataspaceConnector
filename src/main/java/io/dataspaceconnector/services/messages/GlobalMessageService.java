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
package io.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.QueryLanguage;
import de.fraunhofer.iais.eis.QueryScope;
import de.fraunhofer.iais.eis.QueryTarget;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.protocol.multipart.MessageAndPayload;
import de.fraunhofer.ids.messaging.protocol.multipart.mapping.MessageProcessedNotificationMAP;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.broker.BrokerFactory;
import io.dataspaceconnector.repositories.BrokerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

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

    private final @NotNull BrokerRepository brokerRepository;

    private final @NotNull BrokerFactory brokerFactory;

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
        return updateBroker(recipient, response, msg);
    }

    /**
     * Send connector unavailable message and validate received response.
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
    public boolean sendConnectorUnavailableMessage(final URI recipient) throws
            MultipartParseException,
            ClaimsException,
            DapsTokenManagerException,
            IOException {
        final var response = brokerSvc.unregisterAtBroker(recipient);
        final var msg = String.format("Successfully unregistered connector. [url=(%s)]", recipient);
        return updateBroker(recipient, response, msg);
    }

    /**
     * Send resource update message and validate received response.
     *
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
     *
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
    public boolean sendResourceUnavailableMessage(final URI recipient, final Resource resource) throws
            MultipartParseException,
            ClaimsException,
            DapsTokenManagerException,
            IOException {
        final var response = brokerSvc.removeResourceFromBroker(recipient, resource);
        final var msg = String.format("Successfully unregistered resource. "
                + "[resourceId=(%s), url=(%s)]", resource.getId(), recipient);
        return validateResponse(response, msg);
    }

    /**
     * Send query message and validate received response.
     *
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
     *
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
     *
     * @param response The response map.
     * @param logMsg   The log message.
     * @return true if the recipient successfully processed the message, false otherwise.
     */
    private boolean validateResponse(final MessageAndPayload response, final String logMsg) {
        if (response != null) {
            if (log.isInfoEnabled()) {
                log.info(logMsg);
            }
            return true;
        }

        return false;
    }

    /**
     * This method updates the registration status of the broker.
     * @param recipient URI of the recipient
     * @return true if registration status was updated
     */
    private boolean updateRegistrationStatus(final URI recipient) {
        final var allBrokers = brokerRepository.findAll();
        Broker foundBroker = null;
        for (final var broker : allBrokers) {
            if (recipient.equals(broker.getLocation())) {
                foundBroker = broker;
                break;
            }
        }
        if (foundBroker != null) {
            if (RegistrationStatus.UNREGISTERED.equals(foundBroker.getStatus())) {
                brokerFactory.updateRegistrationStatus(foundBroker, RegistrationStatus.REGISTERED);
            } else {
                brokerFactory.updateRegistrationStatus(foundBroker, RegistrationStatus.UNREGISTERED);
            }
            brokerRepository.saveAndFlush(foundBroker);
            return true;
        }
        return false;
    }

    /**
     *
     * @param recipient URI of the recipient
     * @param response The response.
     * @param msg The response message.
     * @return true, if connector is updated at the broker.
     */
    private boolean updateBroker(final URI recipient,
                                 final MessageProcessedNotificationMAP response,
                                 final String msg) {
        final var result = validateResponse(response, msg);
        if (result) {
            final var updatedRegistrationStatus = updateRegistrationStatus(recipient);
            if (updatedRegistrationStatus) {
                if (log.isInfoEnabled()) {
                    log.info("Successfully updated broker registration status.");
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info("Failed to update the registration status of the broker.");
                }
            }
        }
        return result;
    }
}
