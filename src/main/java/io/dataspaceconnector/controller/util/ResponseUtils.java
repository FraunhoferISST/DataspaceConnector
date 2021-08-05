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
package io.dataspaceconnector.controller.util;

import de.fhg.aisec.ids.idscp2.idscp_core.error.Idscp2Exception;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.service.message.handler.dto.Response;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * Contains utility methods for creating ResponseEntities with different status codes and custom
 * messages or exception.
 */
@Log4j2
public final class ResponseUtils {

    /**
     * Default constructor.
     */
    private ResponseUtils() {
        // not used
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that an error occurred
     * in the ids communication.
     *
     * @param e Exception that was thrown during communication.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> respondIdsMessageFailed(final Exception e) {
        final var msg = ErrorMessage.MESSAGE_HANDLING_FAILED;
        if (log.isDebugEnabled()) {
            log.debug("{} [exception=({})]", msg, e.getMessage(), e);
        }
        return new ResponseEntity<>(msg.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that the message could
     * not be built or sent.
     *
     * @param e Exception that was thrown during communication.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> respondMessageSendingFailed(final Exception e) {
        final var msg = ErrorMessage.MESSAGE_SENDING_FAILED;
        if (log.isDebugEnabled()) {
            log.debug("{} [exception=({})]", msg, e.getMessage(), e);
        }
        return new ResponseEntity<>(msg.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that the configuration
     * could not be updated.
     *
     * @param exception The exception that was thrown.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> respondConfigurationUpdateError(
            final Exception exception) {
        final var msg = "Failed to update configuration.";
        if (log.isDebugEnabled()) {
            log.debug("{} [exception=({})]", msg, exception.getMessage(), exception);
        }
        return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 404 and a message indicating that a resource could
     * not be found.
     *
     * @param resourceId ID for that no match was found.
     * @return ResponseEntity with status code 404.
     */
    public static ResponseEntity<Object> respondResourceNotFound(final URI resourceId) {
        final var msg = "Resource not found.";
        if (log.isDebugEnabled()) {
            log.debug("{} [resourceId=({})]", msg, resourceId);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a ResponseEntity with status code 400 and a message indicating that no predefined
     * policy pattern has been recognized.
     *
     * @param exception The exception that was thrown.
     * @return ResponseEntity with status code 400.
     */
    public static ResponseEntity<Object> respondPatternNotIdentified(final Exception exception) {
        final var msg = "Could not identify pattern.";
        if (log.isDebugEnabled()) {
            log.debug("{} [exception({})]", msg, exception.getMessage());
        }
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a ResponseEntity with status code 400 and a message indicating that the input was
     * invalid.
     *
     * @param e The exception that was thrown.
     * @return ResponseEntity with status code 400.
     */
    public static ResponseEntity<Object> respondInvalidInput(final Exception e) {
        final var msg = ErrorMessage.INVALID_INPUT;
        if (log.isWarnEnabled()) {
            log.warn("{} [exception=({})]", msg, e.getMessage(), e);
        }
        return new ResponseEntity<>(String.format("%s %s", msg, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that the contract
     * request could not be built.
     *
     * @param e The exception that was thrown.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> respondFailedToBuildContractRequest(final Exception e) {
        final var msg = "Failed to build contract request.";
        if (log.isWarnEnabled()) {
            log.warn("{} [exception=({})]", msg, e.getMessage(), e);
        }
        return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that saving an entity
     * has failed.
     *
     * @param e The exception that was thrown.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> respondFailedToStoreEntity(final Exception e) {
        final var msg = "Failed to store entity.";
        if (log.isWarnEnabled()) {
            log.warn("{} [exception=({})]", msg, e.getMessage(), e);
        }
        return new ResponseEntity<>(String.format("%s %s", msg, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 504 and a message indicating that the connection
     * timed out.
     *
     * @param exception The exception that was thrown.
     * @return ResponseEntity with status code 504.
     */
    public static ResponseEntity<Object> respondConnectionTimedOut(final Exception exception) {
        final var msg = ErrorMessage.GATEWAY_TIMEOUT;
        if (log.isWarnEnabled()) {
            log.warn("{} [exception=({})]", msg, exception.getMessage(), exception);
        }
        return new ResponseEntity<>(msg.toString(), HttpStatus.GATEWAY_TIMEOUT);
    }

    /**
     * Creates a ResponseEntity with status code 502 and a message indicating that the client
     * received an invalid response.
     *
     * @param exception Exception that was thrown during communication.
     * @return ResponseEntity with status code 502.
     */
    public static ResponseEntity<Object> respondReceivedInvalidResponse(final Exception exception) {
        final var msg = ErrorMessage.INVALID_MESSAGE;
        if (log.isDebugEnabled()) {
            log.debug("{} [exception=({})]", msg, exception.getMessage(), exception);
        }
        return new ResponseEntity<>(msg.toString(), HttpStatus.BAD_GATEWAY);
    }

    /**
     * Creates a ResponseEntity with status code 502 and a message indicating that the client
     * received an invalid response.
     *
     * @return ResponseEntity with status code 502.
     */
    public static ResponseEntity<Object> respondReceivedInvalidResponse() {
        final var msg = ErrorMessage.INVALID_MESSAGE;
        if (log.isDebugEnabled()) {
            log.debug("{}", msg.toString());
        }

        return new ResponseEntity<>(msg.toString(), HttpStatus.BAD_GATEWAY);
    }

    /**
     * Show response message that was not expected.
     *
     * @param response The response map.
     * @return ResponseEntity with status code 417.
     */
    public static ResponseEntity<Object> respondWithContent(final Map<String, Object> response) {
        if (log.isWarnEnabled()) {
            log.warn("Received unexpected response message. [response=({})]", response);
        }
        return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
    }

    /**
     * Creates a ResponseEntity with status code 502 and a message indicating that the contract
     * negotiation has been aborted due to an invalid contract agreement.
     *
     * @return ResponseEntity with status code 502.
     */
    public static ResponseEntity<Object> respondNegotiationAborted() {
        final var msg = "Received invalid agreement during negotiation phase.";
        if (log.isDebugEnabled()) {
            log.debug(msg);
        }
        return new ResponseEntity<>(msg, HttpStatus.BAD_GATEWAY);
    }

    /**
     * Creates a ResponseEntity with status code 204 and a message indicating that no subscription
     * could be found for the targeted entity.
     *
     * @param target The target element.
     * @return ResponseEntity with status code 204.
     */
    public static ResponseEntity<Object> respondNoSubscriptionsFound(final URI target) {
        if (log.isDebugEnabled()) {
            log.debug("No subscriptions found. [target=({})]", target);
        }
        return new ResponseEntity<>("No subscriptions found.", HttpStatus.NO_CONTENT);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that IDSCP2
     * communication failed.
     *
     * @param recipient the intended recipient.
     * @param exception the Idscp2Exception.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> respondIdscp2Error(final URI recipient,
                                                            final Idscp2Exception exception) {
        final var msg = "IDSCP2 communication failed.";
        if (log.isDebugEnabled()) {
            log.debug("{} [recipient=({})] [exception=({})]", msg, recipient,
                    exception.getMessage());
        }
        return new ResponseEntity<>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns the result of a controller's Camel route as a response entity.
     *
     * @param exchange the exchange resulting from the Camel route.
     * @return a response entity representing the result.
     */
    public static ResponseEntity<Object> respondWithExchangeContent(final Exchange exchange) {
        final var response = exchange.getIn().getBody(Response.class);
        if (response != null) {
            return ResponseEntity.ok(response.getBody());
        } else {
            final var responseEntity =
                    toObjectResponse(exchange.getIn().getBody(ResponseEntity.class));
            return Objects.requireNonNullElseGet(responseEntity,
                    () -> new ResponseEntity<>("An internal server error occurred.",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @SuppressWarnings("unchecked")
    private static ResponseEntity<Object> toObjectResponse(final ResponseEntity<?> response) {
        return (ResponseEntity<Object>) response;
    }
}
