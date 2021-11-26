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

import de.fraunhofer.iais.eis.Resource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.common.exception.DataDispatchException;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.net.HttpService;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.common.routing.RouteDataDispatcher;
import io.dataspaceconnector.common.net.ApiReferenceHelper;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.service.ArtifactRetriever;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.util.Event;
import io.dataspaceconnector.service.resource.ids.builder.IdsResourceBuilder;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.service.resource.type.SubscriptionService;
import io.dataspaceconnector.service.usagecontrol.DataAccessVerifier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
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
    private final @NonNull ArtifactRetriever dataReceiver;

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
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorConfig connectorConfig;

    /**
     * Template for triggering Camel routes.
     */
    private final @NonNull ProducerTemplate template;

    /**
     * The CamelContext required for constructing the {@link ProducerTemplate}.
     */
    private final @NonNull CamelContext context;

    /**
     * Helper for creating self links.
     */
    private final @NonNull SelfLinkHelper selfLinkHelper;

    /**
     * Dispatches data via Camel routes.
     */
    private final @NonNull RouteDataDispatcher routeDataDispatcher;

    /**
     * Helper class for managing API endpoint references.
     */
    private final @NonNull ApiReferenceHelper apiReferenceHelper;

    /**
     * Notify subscribers on database update event.
     *
     * @param entity The updated entity.
     */
    public void notifyOnUpdate(final Entity entity) {
        final var uri = selfLinkHelper.getSelfLink(entity);
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
        final var notification = new HashMap<String, String>();
        notification.put("ids-target", target.toString());
        notification.put("ids-event", Event.UPDATED.toString());
        if (!recipients.isEmpty()) {
            sendNotification(recipients, notification, null);
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

    @SuppressFBWarnings(
            value = "REC_CATCH_EXCEPTION",
            justification = "caught exceptions are unchecked"
    )
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
                    if (connectorConfig.isIdscpEnabled()) {
                        final var result = template.send("direct:resourceUpdateSender",
                                ExchangeBuilder.anExchange(context)
                                        .withProperty(ParameterUtils.RECIPIENT_PARAM, recipient)
                                        .withProperty(ParameterUtils.RESOURCE_ID_PARAM,
                                                resource.getId())
                                        .build());
                        final var response = result.getIn().getBody(Response.class);
                        if (response != null) {
                            if (log.isDebugEnabled()) {
                                log.debug("Successfully sent update message. [url=({})]",
                                        recipient);
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("{} [url=({})]", ErrorMessage.UPDATE_MESSAGE_FAILED,
                                        recipient);
                            }
                        }
                    } else {
                        final var response = messageSvc
                                .sendResourceUpdateMessage(recipient, resource);
                        if (response.isPresent()) {
                            if (log.isDebugEnabled()) {
                                log.debug("Successfully sent update message. [url=({})]",
                                        recipient);
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("{} [url=({})]", ErrorMessage.UPDATE_MESSAGE_FAILED,
                                        recipient);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("{} [url=({}), exception=({})]",
                                ErrorMessage.UPDATE_MESSAGE_FAILED, recipient, e.getMessage());
                    }
                }
            }
        }
    }

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
                return artifactSvc.getData(accessVerifier, dataReceiver, id, new QueryInput(),
                        null);
            } catch (IOException exception) {
                if (log.isDebugEnabled()) {
                    log.debug("Failed to retrieve data. [exception=({})]", exception.getMessage());
                }
            }
        }
        return InputStream.nullInputStream();
    }

    private void sendNotification(final List<URI> recipients,
                                  final Map<String, String> notification, final InputStream data) {
        for (final var recipient : recipients) {
            try {
                InputStream dataCopy;
                if (data == null) {
                    dataCopy = InputStream.nullInputStream();
                } else {
                    // Reset the input stream to first position before each read.
                    if (0 <= data.available()) {
                        data.reset();
                    }
                    dataCopy = data;
                }

                if (apiReferenceHelper.isRouteReference(recipient.toURL())) {
                    sendNotificationViaCamel(recipient, notification, dataCopy);
                } else {
                    sendNotificationViaHttp(recipient, notification, dataCopy);
                }
            } catch (IOException exception) {
                if (log.isWarnEnabled()) {
                    log.warn("Could not notify subscriber. [url=({})]",
                            recipient);
                }
            }
        }
    }

    private void sendNotificationViaCamel(final URI recipient,
                                          final Map<String, String> notification,
                                          final InputStream data) {
        try {
            final var queryInput = new QueryInput();
            queryInput.setHeaders(notification);
            routeDataDispatcher.send(recipient, data.readAllBytes(), queryInput);
        } catch (DataDispatchException | IOException exception) {
            if (log.isWarnEnabled()) {
                log.warn("Could not notify subscriber. [url=({}), exception=({})]",
                        recipient, exception.getMessage());
            }
        }
    }

    private void sendNotificationViaHttp(final URI recipient,
                                         final Map<String, String> notification,
                                         final InputStream data) {
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
