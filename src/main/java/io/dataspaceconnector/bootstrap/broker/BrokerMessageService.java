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

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.protocol.multipart.mapping.MessageProcessedNotificationMAP;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Register resources at an IDS Broker.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class BrokerMessageService {

    /**
     * The service for communication with the ids broker.
     */
    private final @NotNull IDSBrokerService brokerService;

    /**
     * Register resources at the broker.
     *
     * @param properties Bootstrap properties.
     * @param resources  The ids resources to register.
     * @return true if all resources could be registered.
     */
    public boolean registerAtBroker(final Properties properties,
                                    final Map<URI, Resource> resources) {
        final var knownBrokers = new HashSet<URL>();
        // Iterate over all registered resources.
        for (final var entry : resources.entrySet()) {
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
        final var response =
                brokerService.updateSelfDescriptionAtBroker(broker.toURI());
        if (validateBrokerResponse(response, broker)) {
            if (log.isInfoEnabled()) {
                log.info("Registered connector at broker. [broker=({})]", broker);
            }
            return true;
        }

        return false;
    }

    private boolean updateAtBroker(final URL broker, final URI key, final Resource resource) throws Exception {
        final var response =
                brokerService.updateResourceAtBroker(broker.toURI(), resource);
        if (validateBrokerResponse(response, broker)) {
            if (log.isInfoEnabled()) {
                log.info("Registered resource at broker. [resourceId=({}), broker=({})]",
                        key.toString(), broker);
            }
            return true;
        }

        return false;
    }

    /**
     * Check if a broker request was successfully processed by a broker.
     *
     * @param response  the broker response
     * @param brokerURL the URL of the called broker
     * @return true if the broker successfully processed the message, false otherwise.
     */
    private boolean validateBrokerResponse(final MessageProcessedNotificationMAP response,
                                           final URL brokerURL) {
        if (response == null) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to sent message to broker. [broker=({})]", brokerURL);
            }
            return false;
        }

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
}
