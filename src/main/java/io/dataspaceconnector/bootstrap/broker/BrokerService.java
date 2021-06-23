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
package io.dataspaceconnector.bootstrap.broker;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessage;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.ids.framework.communication.broker.IDSBrokerService;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import static de.fraunhofer.isst.ids.framework.util.MultipartStringParser.stringToMultipart;

/**
 * Register resources at the broker.
 */
@Log4j2
@Service("io.dataspaceconnector.bootstrap.broker.brokerService")
@RequiredArgsConstructor
public class BrokerService {

    /**
     * The service for communication with the ids broker.
     */
    private final @NotNull IDSBrokerService idsBrokerSvc;

    /**
     * Service for deserializing ids entities.
     */
    private final @NotNull DeserializationService deserializationService;

    /**
     * Register resources at the broker.
     *
     * @param properties   Bootstrap properties.
     * @param idsResources The ids resources to register.
     * @return true if all resources could be registered.
     */
    public boolean registerAtBroker(final Properties properties,
                                    final Map<URI, Resource> idsResources) {
        final var knownBrokers = new HashSet<URL>();
        // iterate over all registered resources
        for (final var entry : idsResources.entrySet()) {
            final var propertyKey = "broker.register." + entry.getKey().toString();
            if (properties.containsKey(propertyKey)) {
                final var brokerURL = toBrokerUrl(properties.getProperty(propertyKey));
                if (brokerURL.isEmpty()) {
                    if (log.isWarnEnabled()) {
                        log.warn("Skipping broker due to invalid URL. [broker=({})]",
                                properties.getProperty(propertyKey));
                    }
                    return false;
                }

                final var broker = brokerURL.get();

                try {
                    if (!knownBrokers.contains(broker)) {
                        knownBrokers.add(broker);
                        if (!registerAtBroker(broker)) {
                            return false;
                        }
                    }

                    if (!updateAtBroker(broker, entry.getKey(), entry.getValue())) {
                        return false;
                    }
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Could not register resource at broker [resourceId=({}), "
                                + "broker=({})].", entry.getKey().toString(), broker, e);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean registerAtBroker(final URL broker) throws Exception {
        final var response = idsBrokerSvc.updateSelfDescriptionAtBroker(broker.toString());
        if (validateBrokerResponse(response, broker)) {
            if (log.isInfoEnabled()) {
                log.info("Registered connector at broker. [broker=({})]", broker);
            }
            return true;
        }
        return false;
    }

    private boolean updateAtBroker(final URL broker, final URI key, final Resource value)
            throws Exception {
        final var response = idsBrokerSvc.updateResourceAtBroker(broker.toString(), value);
        if (!response.isSuccessful()) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update resource at broker. [resourceId=({}), "
                        + "broker=({})]", value.getId().toString(), broker);
            }
            return false;
        }

        if (validateBrokerResponse(response, broker)) {
            if (log.isInfoEnabled()) {
                log.info("Registered resource at broker. [resourceId=({}), broker=({})]",
                        key.toString(), broker);
            }
        } else {
            return false;
        }

        response.close();
        return true;
    }


    private Optional<URL> toBrokerUrl(final String broker) {
        try {
            return Optional.of(new URL(broker));
        } catch (MalformedURLException ignored) {
            // Nothing to do here.
        }
        return Optional.empty();
    }


    /**
     * Check if a broker request was successfully processed by a broker.
     *
     * @param response  the broker response
     * @param brokerURL the URL of the called broker
     * @return true if the broker successfully processed the message, false otherwise
     * @throws Exception if the response's body cannot be extracted as string.
     */
    private boolean validateBrokerResponse(final Response response, final URL brokerURL)
            throws Exception {
        if (!response.isSuccessful()) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to sent message to broker. [broker=({})]", brokerURL);
            }
            return false;
        }

        final var responseBody = response.body();
        if (!validateResponseBody(responseBody)) {
            return false;
        }

        final var body = Objects.requireNonNull(responseBody).string();
        final var responseMessage = getMessage(body);

        if (responseMessage.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Could not parse response after sending request to broker.");
            }
            return false;
        }

        if (!(responseMessage.get() instanceof MessageProcessedNotificationMessage)) {
            if (responseMessage.get() instanceof RejectionMessage) {
                final var payload = stringToMultipart(body).get("payload");
                if (log.isDebugEnabled()) {
                    log.debug("Broker rejected the message. [reason=({}), payload=({})]",
                            MessageUtils.extractRejectionReason(
                                    (RejectionMessage) responseMessage.get()).toString(), payload);
                } else if (log.isDebugEnabled()) {
                    log.debug("Broker rejected the message. [reason=({})]",
                            MessageUtils.extractRejectionReason(
                                    (RejectionMessage) responseMessage.get()).toString());
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("An error occurred while registering the connector at the broker.");
                }
            }
            return false;
        }
        return true;
    }

    private boolean validateResponseBody(final ResponseBody body) {
        if (body == null) {
            if (log.isDebugEnabled()) {
                log.debug("Could not parse response after sending a request to the broker.");
            }
            return false;
        }
        return true;
    }

    /**
     * Extract the IDS message from a multipart message string.
     *
     * @param body a multipart message
     * @return The IDS message contained in the multipart message, null if any error occurs.
     */
    private Optional<Message> getMessage(final String body) throws Exception {
        final var header = stringToMultipart(body).get("header");
        return Optional.of(deserializationService.getMessage(header));
    }
}
