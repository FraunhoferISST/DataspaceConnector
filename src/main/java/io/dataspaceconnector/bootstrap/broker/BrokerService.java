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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

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
import org.apache.commons.fileupload.MultipartStream;
import org.springframework.stereotype.Service;

/**
 * Register resources at the broker.
 */
@Log4j2
@Service
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
     * @param properties Bootstrap properties.
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
                    if (log.isErrorEnabled()) {
                        log.error("Skipping broker '{}'. Not a valid URL.",
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
                } catch (IOException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Could not register resource with IDS id '{}' at the "
                                  + "broker '{}'.", entry.getKey().toString(), broker, e);
                    }

                    return false;
                }
            }
        }

        return true;
    }

    private boolean registerAtBroker(final URL broker) throws IOException {
        final var response = idsBrokerSvc.updateSelfDescriptionAtBroker(broker.toString());
        if (validateBrokerResponse(response, broker)) {
            if (log.isInfoEnabled()) {
                log.info("Registered connector at broker '{}'.", broker);
            }

            return true;
        }

        return false;
    }

    private boolean updateAtBroker(final URL broker, final URI key, final Resource value)
            throws IOException {
        final var response = idsBrokerSvc.updateResourceAtBroker(broker.toString(), value);
        if (!response.isSuccessful()) {
            if (log.isErrorEnabled()) {
                log.error("Failed to update resource description for resource '{}'"
                          + " at broker '{}'.", value.getId().toString(), broker);
            }

            return false;
        }

        if (validateBrokerResponse(response, broker)) {
            if (log.isInfoEnabled()) {
                log.info("Registered resource with IDS ID '{}' at broker '{}'.",
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
     * @throws IOException if the response's body cannot be extracted as string.
     */
    private boolean validateBrokerResponse(final Response response, final URL brokerURL)
            throws IOException {
        if (!response.isSuccessful()) {
            if (log.isErrorEnabled()) {
                log.error("Failed to sent message to a broker '{}'.", brokerURL);
            }

            return false;
        }

        final var responseBody = response.body();
        if (!validateResponseBody(responseBody)) {
            return false;
        }

        final var body = responseBody.string();
        final var responseMessage = getMessage(body);
        if (responseMessage.isPresent()) {
            if (log.isErrorEnabled()) {
                log.error("Could not parse response after sending a request "
                          + "to a broker.");
            }

            return false;
        }

        if (!(responseMessage.get() instanceof MessageProcessedNotificationMessage)) {
            if (responseMessage.get() instanceof RejectionMessage) {
                final var payload = getMultipartPart(body, "payload");
                if (log.isErrorEnabled() && payload.isPresent()) {
                    log.error("The broker rejected the message. Reason: {} - {}",
                              MessageUtils.extractRejectionReason(
                                      (RejectionMessage) responseMessage.get()).toString(),
                              payload.get());
                } else if (log.isErrorEnabled()) {
                    log.error("The broker rejected the message. Reason: {}",
                              MessageUtils.extractRejectionReason(
                                      (RejectionMessage) responseMessage.get()).toString());
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error("An error occurred while registering the "
                              + "connector at the broker.");
                }
            }
            return false;
        }

        return true;
    }

    private boolean validateResponseBody(final ResponseBody body) {
        if (body == null) {
            if (log.isErrorEnabled()) {
                log.error("Could not parse response after sending a request "
                          + "to a broker.");
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
    private Optional<Message> getMessage(final String body) {
        final var part = getMultipartPart(body, "header");
        if (part.isPresent()) {
            return Optional.of(deserializationService.getMessage(part.get()));
        } else {
            if (log.isErrorEnabled()) {
                log.error("Could not find IDS message in multipart message.");
            }

            return Optional.empty();
        }
    }

    /**
     * Extract a part with given name from a multipart message.
     *
     * @param message  the multipart message
     * @param partName the part name
     * @return part with given name, null if the part does not exist in given message
     */
    private Optional<String> getMultipartPart(final String message, final String partName) {
        try {
            // TODO: Can we get the original charset of the message?
            final var multipart = new MultipartStream(
                    new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)),
                    getBoundaries(message)[0].substring(2).getBytes(StandardCharsets.UTF_8),
                    4096,
                    null
            );

            final var pattern = Pattern.compile("name=\"([a-zA-Z]+)\"");
            final var outputStream = new ByteArrayOutputStream();
            boolean next = multipart.skipPreamble();
            while (next) {
                final var matcher = pattern.matcher(multipart.readHeaders());
                if (!matcher.find()) {
                    if (log.isErrorEnabled()) {
                        log.error("Could not find name of multipart part.");
                    }
                    return Optional.empty();
                }

                if (matcher.group().equals("name=\"" + partName + "\"")) {
                    multipart.readBodyData(outputStream);
                    return Optional.of(outputStream.toString(StandardCharsets.UTF_8));
                } else {
                    multipart.discardBodyData();
                }

                next = multipart.readBoundary();
            }

        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to parse multipart message.", e);
            }
            return Optional.empty();
        }

        if (log.isErrorEnabled()) {
            log.error("Could not find part '{}' in multipart message.", partName);
        }

        return Optional.empty();
    }

    private String[] getBoundaries(final String msg) {
        return msg.split(msg.contains("\r\n") ? "\r\n" : "\n");
    }
}
