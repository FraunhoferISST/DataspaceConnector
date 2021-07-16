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

import de.fraunhofer.iais.eis.QueryLanguage;
import de.fraunhofer.iais.eis.QueryScope;
import de.fraunhofer.iais.eis.QueryTarget;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.common.DeserializeException;
import de.fraunhofer.ids.messaging.common.SerializeException;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.protocol.http.SendMessageException;
import de.fraunhofer.ids.messaging.protocol.http.ShaclValidatorException;
import de.fraunhofer.ids.messaging.protocol.multipart.UnknownResponseException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.service.configuration.BrokerService;
import io.dataspaceconnector.service.configuration.EntityLinkerService;
import io.dataspaceconnector.util.UUIDUtils;
import de.fraunhofer.ids.messaging.requests.MessageContainer;
import de.fraunhofer.ids.messaging.requests.exceptions.NoTemplateProvidedException;
import de.fraunhofer.ids.messaging.requests.exceptions.RejectionException;
import de.fraunhofer.ids.messaging.requests.exceptions.UnexpectedPayloadException;
import io.dataspaceconnector.service.message.type.NotificationService;
import io.dataspaceconnector.util.ControllerUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * Service for sending notification messages.
     */
    private final @NonNull NotificationService notificationSvc;

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
    public Optional<MessageContainer<?>>  sendConnectorUpdateMessage(final URI recipient) throws
            MultipartParseException,
            ClaimsException,
            DapsTokenManagerException,
            IOException,
            ShaclValidatorException,
            SerializeException,
            RejectionException,
            UnknownResponseException,
            SendMessageException,
            NoTemplateProvidedException,
            UnexpectedPayloadException,
            DeserializeException {
        final var response = brokerSvc.updateSelfDescriptionAtBroker(recipient);
        final var result = checkResponse(Optional.ofNullable(response));
        if (result) {
            if (log.isInfoEnabled()) {
                log.info(String.format(
                        "Successfully registered connector. [url=(%s)]",
                        recipient
                ));
            }
            brokerService.setRegistrationStatus(recipient, RegistrationStatus.REGISTERED);
        }
        return Optional.ofNullable(response);
    }

    /**
     * Send connector unavailable message and validate received response.
     *
     * @param recipient The recipient.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendConnectorUnavailableMessage(final URI recipient)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        final var response = brokerSvc.unregisterAtBroker(recipient);
        final var result = checkResponse(Optional.ofNullable(response));
        if (result) {
            if (log.isInfoEnabled()) {
                log.info(String.format(
                        "Successfully unregistered connector. [url=(%s)]",
                        recipient
                ));
            }
            brokerService.setRegistrationStatus(recipient, RegistrationStatus.UNREGISTERED);
        }
        return Optional.ofNullable(response);
    }

    /**
     * Send resource update message.
     *
     * @param recipient The recipient.
     * @param resource  The ids resource that should be updated.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendResourceUpdateMessage(final URI recipient,
                                                                   final Resource resource)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        final var response = brokerSvc.updateResourceAtBroker(recipient, resource);
        final var result =  checkResponse(Optional.ofNullable(response));
        if (result) {
            if (log.isInfoEnabled()) {
                log.info(String.format("Successfully registered resource. "
                        + "[resourceId=(%s), url=(%s)]", resource.getId(), recipient)
                );
            }
            updateOfferedResourceBrokerList(recipient, resource);
        }
        return Optional.ofNullable(response);
    }

    /**
     * Send resource unavailable message and validate received response.
     *
     * @param recipient The recipient.
     * @param resource  The ids resource that should be updated.
     * @return True if the message was successfully processed by the recipient, false if not.
     */
    public Optional<MessageContainer<?>> sendResourceUnavailableMessage(final URI recipient,
                                                                        final Resource resource)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        final var response = brokerSvc.removeResourceFromBroker(recipient, resource);
        final var result = checkResponse(Optional.ofNullable(response));
        if (result) {
            if (log.isInfoEnabled()) {
                log.info(String.format("Successfully unregistered resource. "
                        + "[resourceId=(%s), url=(%s)]", resource.getId(), recipient)
                );
            }
            removeBrokerFromOfferedResourceBrokerList(recipient, resource);
        }
        return Optional.ofNullable(response);
    }

    /**
     * Send query message and validate received response.
     * @param recipient The recipient.
     * @param query     The query statement.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendQueryMessage(final URI recipient, final String query)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        final var response = brokerSvc.queryBroker(recipient, query,
                QueryLanguage.SPARQL, QueryScope.ALL, QueryTarget.BROKER);
        return Optional.of(response);
    }

    /**
     * Send query message and validate received response.
     * @param recipient The recipient.
     * @param term      The search term.
     * @param limit     The limit value.
     * @param offset    The offset value.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendFullTextSearchMessage(
            final URI recipient, final String term, final int limit, final int offset)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        final var response = brokerSvc.fullTextSearchBroker(recipient, term,
                QueryScope.ALL, QueryTarget.BROKER, limit, offset);
        return Optional.of(response);
    }

    /**
     * Check if a request was successfully processed by the recipient.
     * Validates response. Returns response entity with status code 200 if a
     * MessageProcessedNotificationMessage has been received, responds with the message's content
     * if not.
     *
     * @param response The response container.
     * @param msgType  Expected message type.
     * @return ResponseEntity with status code.
     */
    public ResponseEntity<Object> validateResponse(final Optional<MessageContainer<?>> response,
                                                   final Class<?> msgType) {
        if (response.isEmpty()) {
            return ControllerUtils.respondReceivedInvalidResponse();
        }

        final var header = response.get().getUnderlyingMessage();
        final var payload = response.get().getReceivedPayload();
        if (header.getClass().equals(msgType)) {
            return new ResponseEntity<>(payload, HttpStatus.OK);
        }

        // If response message is not of type MessageProcessedNotificationMessage.
        final var content = notificationSvc.getResponseContent(header, payload);
        return ControllerUtils.respondWithContent(content);
    }

    /**
     * @param response The response container.
     * @return true, if response is successful.
     */
    public boolean checkResponse(final Optional<MessageContainer<?>> response) {
        if (response.isEmpty()) {
            return false;
        }
        final var resp = response.get();
        return !resp.isRejection() && resp.getUnderlyingMessage() != null;
        // If response message is empty of rejection.
    }

    /**
     * @param recipient The uri of the recipient.
     * @param resource The offered resource.
     */
    private void updateOfferedResourceBrokerList(final URI recipient,
                                                 final Resource resource) {
        final var brokerId = brokerService.findByLocation(recipient);
        if (brokerId.isPresent()) {
            linker.add(brokerId.get(), Set.of(UUIDUtils.uuidFromUri(resource.getId())));
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Updated Resource at Broker but Broker was not linked "
                        + "to resource in OfferedResource-Broker-List.");
            }
        }
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
