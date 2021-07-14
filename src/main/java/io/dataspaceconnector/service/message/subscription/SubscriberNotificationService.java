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
package io.dataspaceconnector.service.message.subscription;

import de.fraunhofer.iais.eis.Resource;
import io.dataspaceconnector.controller.util.Event;
import io.dataspaceconnector.controller.util.Notification;
import io.dataspaceconnector.model.AbstractEntity;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.model.QueryInput;
import io.dataspaceconnector.model.Subscription;
import io.dataspaceconnector.service.BlockingArtifactReceiver;
import io.dataspaceconnector.service.message.GlobalMessageService;
import io.dataspaceconnector.service.resource.ArtifactService;
import io.dataspaceconnector.service.resource.RequestedResourceService;
import io.dataspaceconnector.service.usagecontrol.DataAccessVerifier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides methods for handling subscriptions to a requested resource.
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class SubscriberNotificationService {

    /**
     * The service for managing requested resources.
     */
    private final @NonNull RequestedResourceService resourceSvc;

    /**
     * Service for sending ids messages.
     */
    private final @NonNull GlobalMessageService messageSvc;

    /**
     * The service managing artifacts.
     */
    private final @NonNull ArtifactService artifactSvc;

    /**
     * The receiver for getting data from a remote source.
     */
    private final @NonNull BlockingArtifactReceiver dataReceiver;

    /**
     * The verifier for the data access.
     */
    private final @NonNull DataAccessVerifier accessVerifier;

    /**
     * Notifies all backend systems subscribed for updates to a requested resource using a
     * {@link SubscriberNotificationRunner}. The backends are notified in parallel and
     * asynchronously. If a request to one of the subscribed URLs results in a status code 5xx,
     * the request is retried 5 times with a delay of 5 seconds each.
     *
     * @param remoteId the remote ID of the requested resource that was updated.
     */
    public void notifySubscribers(final URI remoteId) {
        final var resourceId = resourceSvc.identifyByRemoteId(remoteId);
        if (resourceId.isEmpty()) {
            if (log.isErrorEnabled()) {
                log.error("Could not notify backends about updated resource with remote ID {}: "
                        + "Resource not found.", remoteId);
            }
            return;
        }

        final var resource = resourceSvc.get(resourceId.get());
        final var subscribers = (resource.getSubscriptions() != null
                ? new ArrayList<>(resource.getSubscriptions()) : new ArrayList<Subscription>())
                .stream()
                .map(Subscription::getUrl)
                .collect(Collectors.toList());

//        if (!subscribers.isEmpty()) {
//            new Thread(new SubscriberNotificationRunner(resource.getId(), subscribers)).start();
//        }
    }

    // TODO
    public void notifySubscribers(final List<Subscription> subscriptions, final URI target,
                                  final AbstractEntity entity) {
        final var data = retrieveDataByArtifact(entity);

        // Get list of non-ids subscribers.
        final var recipients = subscriptions.stream()
                .filter(subscription -> !subscription.isIdsProtocol())
                .map(Subscription::getUrl)
                .collect(Collectors.toList());

        // Update non-ids subscribers.
        final var notification = new Notification(new Date(), target, Event.UPDATED);
        if (!recipients.isEmpty()) {
            new Thread(new SubscriberNotificationRunner(notification, recipients, data)).start();
        }

        final var idsRecipients = subscriptions.stream()
                .filter(Subscription::isIdsProtocol)
                .map(Subscription::getUrl)
                .collect(Collectors.toList());

        Resource resource = null;
        for (final var recipient : idsRecipients) {
            try {
                final var update = messageSvc.sendResourceUpdateMessage(recipient, resource);
                if (update) {
                    if (log.isDebugEnabled()) {
                        log.debug("Successfully sent update message. [url=({})]", recipient);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Failed to send update message. [url=({})]", recipient);
                        }
                    }
                }
            } catch (Exception exception) {
                if (log.isWarnEnabled()) {
                    log.debug("Failed to send update message. [url=({}), exception=({})]",
                            recipient, exception.getMessage());
                }
            }
        }

    }

    /**
     * Retrieve data if the entity is of type {@link Artifact}.
     *
     * @param entity The database entity.
     * @return Data as input stream.
     */
    private InputStream retrieveDataByArtifact(final AbstractEntity entity) {
        if (entity instanceof Artifact) {
            final var id = entity.getId();
            try {
                return artifactSvc.getData(accessVerifier, dataReceiver, id, new QueryInput());
            } catch (IOException exception) {
                log.debug("Failed to retrieve data. [exception=({})]", exception.getMessage());
            }
        }
        return null;
    }
}
