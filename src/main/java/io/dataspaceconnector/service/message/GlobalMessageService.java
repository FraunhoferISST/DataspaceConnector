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
import de.fraunhofer.ids.messaging.requests.MessageContainer;
import de.fraunhofer.ids.messaging.requests.exceptions.NoTemplateProvidedException;
import de.fraunhofer.ids.messaging.requests.exceptions.RejectionException;
import de.fraunhofer.ids.messaging.requests.exceptions.UnexpectedPayloadException;
import io.dataspaceconnector.controller.util.ResponseUtils;
import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.service.message.builder.type.NotificationService;
import io.dataspaceconnector.service.resource.type.BrokerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * Service for the broker.
     */
    private final @NotNull BrokerService brokerService;

    /**
     * Service for sending notification messages.
     */
    private final @NonNull NotificationService notificationSvc;

    /**
     * Service for handling broker communication.
     */
    private final @NonNull BrokerCommunication brokerCommunication;

    /**
     * Send connector update message and validate received response.
     *
     * @param input The input uri.
     * @return Optional of message container providing the received ids response.
     * @throws MultipartParseException     if response could not be parsed to header and payload.
     * @throws ClaimsException             if an errors occur while validating a DAT from response.
     * @throws DapsTokenManagerException   if the DAT for building the message cannot be acquired.
     * @throws IOException                 if any other problem in establishing a connection occurs.
     * @throws ShaclValidatorException     if received header did not pass SHACL validation.
     * @throws SerializeException          if serializing an outgoing message fails.
     * @throws RejectionException          if the response is a rejection message.
     * @throws UnknownResponseException    if response header cannot be cast to known message type.
     * @throws SendMessageException        if recipient could not be reached.
     * @throws NoTemplateProvidedException if not matching template for message building was found.
     * @throws UnexpectedPayloadException  if payload could not be processed.
     * @throws DeserializeException        if serializing an incoming response message fails.
     */
    public Optional<MessageContainer<?>> sendConnectorUpdateMessage(final URI input)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        // Check if input was a broker id or an url.
        final var address = brokerCommunication.checkInput(input);
        final var response = brokerSvc.updateSelfDescriptionAtBroker(address);

        // Validate response.
        final var result = checkResponse(Optional.ofNullable(response));
        if (result) {
            if (log.isInfoEnabled()) {
                log.info("Successfully updated connector. [url=({}})]", input);
            }
            brokerService.setRegistrationStatus(address, RegistrationStatus.REGISTERED);
        }

        return Optional.ofNullable(response);
    }

    /**
     * Send connector unavailable message and validate received response.
     *
     * @param input The recipient or a broker id.
     * @return Optional of message container providing the received ids response.
     * @throws MultipartParseException     if response could not be parsed to header and payload.
     * @throws ClaimsException             if an errors occur while validating a DAT from response.
     * @throws DapsTokenManagerException   if the DAT for building the message cannot be acquired.
     * @throws IOException                 if any other problem in establishing a connection occurs.
     * @throws ShaclValidatorException     if received header did not pass SHACL validation.
     * @throws SerializeException          if serializing an outgoing message fails.
     * @throws RejectionException          if the response is a rejection message.
     * @throws UnknownResponseException    if response header cannot be cast to known message type.
     * @throws SendMessageException        if recipient could not be reached.
     * @throws NoTemplateProvidedException if not matching template for message building was found.
     * @throws UnexpectedPayloadException  if payload could not be processed.
     * @throws DeserializeException        if serializing an incoming response message fails.
     */
    public Optional<MessageContainer<?>> sendConnectorUnavailableMessage(final URI input)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        // Check if input was a broker id or an url.
        final var address = brokerCommunication.checkInput(input);
        final var response = brokerSvc.unregisterAtBroker(address);

        // Validate response.
        final var result = checkResponse(Optional.ofNullable(response));
        if (result) {
            if (log.isInfoEnabled()) {
                log.info("Successfully unregistered connector. [url=({}})]", input);
            }
            brokerService.setRegistrationStatus(address, RegistrationStatus.UNREGISTERED);
        }
        return Optional.ofNullable(response);
    }

    /**
     * Send resource update message.
     *
     * @param input    The recipient or a broker id.
     * @param resource The ids resource that should be updated.
     * @return Optional of message container providing the received ids response.
     * @throws MultipartParseException     if response could not be parsed to header and payload.
     * @throws ClaimsException             if an errors occur while validating a DAT from response.
     * @throws DapsTokenManagerException   if the DAT for building the message cannot be acquired.
     * @throws IOException                 if any other problem in establishing a connection occurs.
     * @throws ShaclValidatorException     if received header did not pass SHACL validation.
     * @throws SerializeException          if serializing an outgoing message fails.
     * @throws RejectionException          if the response is a rejection message.
     * @throws UnknownResponseException    if response header cannot be cast to known message type.
     * @throws SendMessageException        if recipient could not be reached.
     * @throws NoTemplateProvidedException if not matching template for message building was found.
     * @throws UnexpectedPayloadException  if payload could not be processed.
     * @throws DeserializeException        if serializing an incoming response message fails.
     */
    public Optional<MessageContainer<?>> sendResourceUpdateMessage(final URI input,
                                                                   final Resource resource)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        // Check if input was a broker id or an url.
        final var address = brokerCommunication.checkInput(input);
        final var response = brokerSvc.updateResourceAtBroker(address, resource);

        // Validate response.
        final var result = checkResponse(Optional.ofNullable(response));
        if (result) {
            if (log.isInfoEnabled()) {
                log.info("Successfully updated resource. [resourceId=({}}), url=({}})]",
                        resource.getId(), input);
            }

            brokerCommunication.updateOfferedResourceBrokerList(address, resource);
        }
        return Optional.ofNullable(response);
    }

    /**
     * Send resource unavailable message and validate received response.
     *
     * @param input    The input uri.
     * @param resource The ids resource that should be updated.
     * @return Optional of message container providing the received ids response.
     * @throws MultipartParseException     if response could not be parsed to header and payload.
     * @throws ClaimsException             if an errors occur while validating a DAT from response.
     * @throws DapsTokenManagerException   if the DAT for building the message cannot be acquired.
     * @throws IOException                 if any other problem in establishing a connection occurs.
     * @throws ShaclValidatorException     if received header did not pass SHACL validation.
     * @throws SerializeException          if serializing an outgoing message fails.
     * @throws RejectionException          if the response is a rejection message.
     * @throws UnknownResponseException    if response header cannot be cast to known message type.
     * @throws SendMessageException        if recipient could not be reached.
     * @throws NoTemplateProvidedException if not matching template for message building was found.
     * @throws UnexpectedPayloadException  if payload could not be processed.
     * @throws DeserializeException        if serializing an incoming response message fails.
     */
    public Optional<MessageContainer<?>> sendResourceUnavailableMessage(final URI input,
                                                                        final Resource resource)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        // Check if input was a broker id or an url.
        final var address = brokerCommunication.checkInput(input);
        final var response = brokerSvc.removeResourceFromBroker(address, resource);

        // Validate response.
        final var result = checkResponse(Optional.ofNullable(response));
        if (result) {
            if (log.isInfoEnabled()) {
                log.info("Successfully unregistered resource. [resourceId=({}}), url=({}})]",
                        resource.getId(), input);
            }
            brokerCommunication.removeBrokerFromOfferedResourceBrokerList(address, resource);
        }
        return Optional.ofNullable(response);
    }

    /**
     * Send query message and validate received response.
     *
     * @param input The recipient or a broker id.
     * @param query The query statement.
     * @return Optional of message container providing the received ids response.
     * @throws MultipartParseException     if response could not be parsed to header and payload.
     * @throws ClaimsException             if an errors occur while validating a DAT from response.
     * @throws DapsTokenManagerException   if the DAT for building the message cannot be acquired.
     * @throws IOException                 if any other problem in establishing a connection occurs.
     * @throws ShaclValidatorException     if received header did not pass SHACL validation.
     * @throws SerializeException          if serializing an outgoing message fails.
     * @throws RejectionException          if the response is a rejection message.
     * @throws UnknownResponseException    if response header cannot be cast to known message type.
     * @throws SendMessageException        if recipient could not be reached.
     * @throws NoTemplateProvidedException if not matching template for message building was found.
     * @throws UnexpectedPayloadException  if payload could not be processed.
     * @throws DeserializeException        if serializing an incoming response message fails.
     */
    public Optional<MessageContainer<?>> sendQueryMessage(final URI input, final String query)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        // Check if input was a broker id or an url.
        final var address = brokerCommunication.checkInput(input);
        final var response = brokerSvc.queryBroker(address, query,
                QueryLanguage.SPARQL, QueryScope.ALL, QueryTarget.BROKER);


        return Optional.of(response);
    }

    /**
     * Send query message and validate received response.
     *
     * @param input  The recipient or a broker id.
     * @param term   The search term.
     * @param limit  The limit value.
     * @param offset The offset value.
     * @return Optional of message container providing the received ids response.
     * @throws MultipartParseException     if response could not be parsed to header and payload.
     * @throws ClaimsException             if an errors occur while validating a DAT from response.
     * @throws DapsTokenManagerException   if the DAT for building the message cannot be acquired.
     * @throws IOException                 if any other problem in establishing a connection occurs.
     * @throws ShaclValidatorException     if received header did not pass SHACL validation.
     * @throws SerializeException          if serializing an outgoing message fails.
     * @throws RejectionException          if the response is a rejection message.
     * @throws UnknownResponseException    if response header cannot be cast to known message type.
     * @throws SendMessageException        if recipient could not be reached.
     * @throws NoTemplateProvidedException if not matching template for message building was found.
     * @throws UnexpectedPayloadException  if payload could not be processed.
     * @throws DeserializeException        if serializing an incoming response message fails.
     */
    public Optional<MessageContainer<?>> sendFullTextSearchMessage(
            final URI input, final String term, final int limit, final int offset)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        // Check if input was a broker id or an url.
        final var address = brokerCommunication.checkInput(input);
        final var response = brokerSvc.fullTextSearchBroker(address, term,
                QueryScope.ALL, QueryTarget.BROKER, limit, offset);
        return Optional.of(response);
    }

    /**
     * Check if a request was successfully processed by the recipient. Validates response.
     * Returns response entity with status code 200 if a MessageProcessedNotificationMessage has
     * been received, responds with the message's content if not.
     *
     * @param response The response container.
     * @param msgType  Expected message type.
     * @return ResponseEntity with status code.
     */
    public ResponseEntity<Object> validateResponse(final Optional<MessageContainer<?>> response,
                                                   final Class<?> msgType) {
        if (response.isEmpty()) {
            return ResponseUtils.respondReceivedInvalidResponse();
        }

        final var header = response.get().getUnderlyingMessage();
        final var payload = response.get().getReceivedPayload();
        if (header.getClass().equals(msgType)) {
            return new ResponseEntity<>(payload, HttpStatus.OK);
        }

        // If response message is not of predefined type.
        final var content = notificationSvc.getResponseContent(header, payload);
        return ResponseUtils.respondWithContent(content);
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
}
