package io.dataspaceconnector.bootstrap.broker;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
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
import org.apache.commons.fileupload.MultipartStream;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class BrokerService {

    /**
     * The service for communication with the ids broker.
     */
    private final @NotNull IDSBrokerService brokerService;


    /**
     * Service for deserializing ids entities.
     */
    private final @NotNull DeserializationService deserializationService;

    public boolean registerAtBroker(final Properties properties,
                                     final Map<URI, Resource> idsResources) {
        final var knownBrokers = new HashSet<String>();
        // iterate over all registered resources
        for (final var entry : idsResources.entrySet()) {
            final var propertyKey = "broker.register." + entry.getKey().toString();
            if (properties.containsKey(propertyKey)) {
                final var brokerURL = (String) properties.get(propertyKey);

                try {
                    if (!knownBrokers.contains(brokerURL)) {
                        knownBrokers.add(brokerURL);
                        final var response = brokerService.updateSelfDescriptionAtBroker(brokerURL);
                        if (validateBrokerResponse(response, brokerURL)) {
                            if (log.isInfoEnabled()) {
                                log.info("Registered connector at broker '{}'.", brokerURL);
                            }
                        } else {
                            return false;
                        }
                    }

                    final var response = brokerService.updateResourceAtBroker(brokerURL,
                                                                              entry.getValue());
                    if (!response.isSuccessful()) {
                        if (log.isErrorEnabled()) {
                            log.error("Failed to update resource description for resource '{}'"
                                      + " at broker '{}'.",
                                      entry.getValue().getId().toString(), brokerURL);
                        }

                        return false;
                    }
                    if (validateBrokerResponse(response, brokerURL)) {
                        if (log.isInfoEnabled()) {
                            log.info("Registered resource with IDS ID '{}' at broker '{}'.",
                                     entry.getKey().toString(), brokerURL);
                        }
                    } else {
                        return false;
                    }

                    response.close();
                } catch (IOException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Could not register resource with IDS id '{}' at the "
                                  + "broker '{}'.", entry.getKey().toString(), brokerURL, e);
                    }

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Check if a broker request was successfully processed by a broker.
     *
     * @param response  the broker response
     * @param brokerURL the URL of the called broker
     * @return true if the broker successfully processed the message, false otherwise
     * @throws IOException if the response's body cannot be extracted as string.
     */
    private boolean validateBrokerResponse(final Response response, final String brokerURL)
            throws IOException {
        if (!response.isSuccessful()) {
            if (log.isErrorEnabled()) {
                log.error("Failed to sent message to a broker '{}'.", brokerURL);
            }

            return false;
        }

        final var responseBody = response.body();
        if (responseBody == null) {
            if (log.isErrorEnabled()) {
                log.error("Could not parse response after sending a request "
                          + "to a broker.");
            }

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

        if (!(responseMessage.get() instanceof MessageProcessedNotificationMessage )) {
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
