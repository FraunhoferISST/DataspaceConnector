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
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.service.BlockingArtifactReceiver;
import io.dataspaceconnector.service.HttpService;
import io.dataspaceconnector.service.ids.builder.IdsResourceBuilder;
import io.dataspaceconnector.service.message.GlobalMessageService;
import io.dataspaceconnector.service.resource.ArtifactService;
import io.dataspaceconnector.service.resource.SubscriptionService;
import io.dataspaceconnector.service.usagecontrol.DataAccessVerifier;
import io.dataspaceconnector.util.ErrorMessage;
import io.dataspaceconnector.util.QueryInput;
import io.dataspaceconnector.util.SelfLinkHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class provides methods for handling subscriptions to a requested resource.
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class SubscriberNotificationService {

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
     * Service for handling subscriptions.
     */
    private final @NonNull SubscriptionService subscriptionSvc;

    /**
     * Service for mapping dsc resource to ids resource.
     */
    private final @NonNull IdsResourceBuilder<OfferedResource> resourceBuilder;

    /**
     * Service for executing http requests.
     */
    private final @NonNull HttpService httpService;

    /**
     * Notify subscribers on database update event.
     *
     * @param entity The updated entity.
     */
    public void notifyOnUpdate(final Entity entity) {
        final var uri = SelfLinkHelper.getSelfLink(entity);
        final var subscriptions = subscriptionSvc.getByTarget(uri);

        // Notify subscribers of child elements.
        if (entity instanceof OfferedResource || entity instanceof RequestedResource) {
            final var representations =
                    ((io.dataspaceconnector.model.resource.Resource) entity).getRepresentations();
            for (final var rep : representations) {
                notifyOnUpdate(rep);
            }
        } else if (entity instanceof Representation) {
            final var artifacts = ((Representation) entity).getArtifacts();
            for (final var artifact : artifacts) {
                notifyOnUpdate(artifact);
            }
        }

        notifyAll(subscriptions, uri, entity);
    }

    /**
     * Notifies all backend systems and ids participants that subscribed for updates to an entity.
     *
     * @param subscriptions List of subscriptions for a certain target.
     * @param target        The target of the subscriptions.
     * @param entity        The target entity of the subscriptions.
     */
    public void notifyAll(final List<Subscription> subscriptions, final URI target,
                          final Entity entity) {
        notifySubscribers(subscriptions, target, entity);
        notifyIdsSubscribers(subscriptions, entity);
    }


    private void notifySubscribers(final List<Subscription> subscriptions, final URI target,
                                   final Entity entity) {
        // Get list of non-ids subscribers.
        final var recipients = subscriptions.stream()
                .filter(subscription -> !subscription.isIdsProtocol() && !subscription.isPushData())
                .map(Subscription::getLocation)
                .collect(Collectors.toList());

        // Get list of non-ids subscribers with isPushData == true.
        final var recipientsWithData = subscriptions.stream()
                .filter(subscription -> !subscription.isIdsProtocol() && subscription.isPushData())
                .map(Subscription::getLocation)
                .collect(Collectors.toList());

        // Update non-ids subscribers.
        // final var notification = new Notification(new Date(), target, Event.UPDATED);
        final var notification = new HashMap<String, String>() {{
            put("ids-target", target.toString());
            put("ids-event", Event.UPDATED.toString());
        }};
        if (!recipients.isEmpty()) {
            sendNotification(recipients, notification, InputStream.nullInputStream());
        }

        // Only send data if entity is of type artifact.
        if (!recipientsWithData.isEmpty()) {
            if (entity instanceof Artifact) {
                sendNotification(recipientsWithData, notification, retrieveDataByArtifact(entity));
            } else {
                sendNotification(recipientsWithData, notification, InputStream.nullInputStream());
            }
        }
    }

    private void notifyIdsSubscribers(final List<Subscription> subscriptions, final Entity entity) {
        final var idsRecipients = subscriptions.stream()
                .filter(Subscription::isIdsProtocol)
                .map(Subscription::getLocation)
                .collect(Collectors.toList());

        final var resources = getIdsResourcesFromEntity(entity);

        // Iterate over all recipients and send ids resource update messages.
        for (final var recipient : idsRecipients) {
            // Send update message for every found resource.
            for (final var resource : resources) {
                try {
                    final var response = messageSvc.sendResourceUpdateMessage(recipient, resource);
                    if (response.isPresent()) {
                        if (log.isDebugEnabled()) {
                            log.debug("Successfully sent update message. [url=({})]", recipient);
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("{} [url=({})]", ErrorMessage.UPDATE_MESSAGE_FAILED,
                                    recipient);
                        }
                    }
                } catch (Exception e) {
                    if (log.isWarnEnabled()) {
                        log.debug("{} [url=({}), exception=({})]",
                                ErrorMessage.UPDATE_MESSAGE_FAILED, recipient, e.getMessage());
                    }
                }
            }
        }
    }

    // TODO refactor to recursive method calls
    private List<Resource> getIdsResourcesFromEntity(final Entity entity) {
        var updatedResources = new ArrayList<Resource>();
        if (entity instanceof OfferedResource) {
            updatedResources.add(resourceBuilder.create((OfferedResource) entity));
        } else if (entity instanceof Representation) {
            // Get all resources linked to given representation.
            final var resources = ((Representation) entity).getResources();
            for (final var resource : resources) {
                // Don't add requested resources to that list as ids participants should only know
                // about offered resources.
                if (resource instanceof OfferedResource) {
                    updatedResources.add(resourceBuilder.create((OfferedResource) resource));
                }
            }
        } else if (entity instanceof Artifact) {
            // Get all representations linked to given representation.
            final var representations = ((Artifact) entity).getRepresentations();
            for (final var representation : representations) {
                final var resources = representation.getResources();
                for (final var resource : resources) {
                    // Don't add requested resources to that list as ids participants should only
                    // know about offered resources.
                    if (resource instanceof OfferedResource) {
                        updatedResources.add(resourceBuilder.create((OfferedResource) resource));
                    }
                }
            }
        }
        return updatedResources;
    }

    /**
     * Retrieve data if the entity is of type {@link Artifact}.
     *
     * @param entity The database entity.
     * @return Data as input stream.
     */
    private InputStream retrieveDataByArtifact(final Entity entity) {
        if (entity instanceof Artifact) {
            final var id = entity.getId();
            try {
                return artifactSvc.getData(accessVerifier, dataReceiver, id, new QueryInput());
            } catch (IOException exception) {
                log.debug("Failed to retrieve data. [exception=({})]", exception.getMessage());
            }
        }
        return InputStream.nullInputStream();
    }

    private void sendNotification(final List<URI> recipients,
                                  final Map<String, String> notification, final InputStream data) {
        for (final var recipient : recipients) {
            final var args = new HttpService.HttpArgs();
            args.setHeaders(notification);
            try {
                httpService.post(recipient.toURL(), args, data);
            } catch (IOException exception) {
                if (log.isWarnEnabled()) {
                    log.warn("Could not notify subscriber. [url=({})]", recipient);
                }
            }
        }
    }
}
