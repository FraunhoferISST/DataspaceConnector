package io.dataspaceconnector.services.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides methods for handling subscriptions to a requested resource.
 */
@Log4j2
@Service
public class SubscriberNotificationService {

    /**
     * The service for managing requested resources.
     */
    private final RequestedResourceService requestedResourceService;

    /**
     * Contructs a SubscriberNotificationService.
     *
     * @param requestedResourceService the service for managing requested resources.
     */
    @Autowired
    public SubscriberNotificationService(final RequestedResourceService requestedResourceService) {
        this.requestedResourceService = requestedResourceService;
    }

    /**
     * Adds a URL to the list of subscribers for a given resource.
     *
     * @param resourceId the UUID of the resource.
     * @param uri the URL.
     */
    public void addSubscription(final UUID resourceId, final URI uri) {
        Utils.requireNonNull(uri, ErrorMessages.URI_NULL);
        final var requestedResource = requestedResourceService.get(resourceId);

        var subscribers = requestedResource.getSubscribers();
        if (subscribers == null) {
            subscribers = new ArrayList<>();
        }

        if (!subscribers.contains(uri)) {
            subscribers.add(uri);
            requestedResourceService.updateSubscriptions(resourceId, subscribers);
        }
    }

    /**
     * Removes a URL from the list of subscribers to a given resource.
     *
     * @param resourceId the UUID of the resource.
     * @param uri the URL.
     */
    public void removeSubscription(final UUID resourceId, final URI uri) {
        Utils.requireNonNull(uri, ErrorMessages.URI_NULL);
        final var requestedResource = requestedResourceService.get(resourceId);

        var subscribers = requestedResource.getSubscribers();
        if (subscribers != null && subscribers.contains(uri)) {
            subscribers.remove(uri);
            requestedResourceService.updateSubscriptions(resourceId, subscribers);
        }
    }

    /**
     * Notifies all backend systems subscribed for updates to a requested resource using a
     * {@link SubscriberNotificationRunner}. The backends are notified in parallel and
     * asynchronously. If a request to one of the subscribed URLs results in a status code 5xx,
     * the request is retried 5 times with a delay of 5 seconds each.
     *
     * @param remoteId the remote ID of the requested resource that was updated.
     */
    public void notifySubscribers(final URI remoteId) {
        final var resourceId = requestedResourceService.identifyByRemoteId(remoteId);
        if (resourceId.isEmpty()) {
            if (log.isErrorEnabled()) {
                log.error("Could not notify backends about updated resource with remote ID {}: "
                        + "Resource not found.", remoteId);
            }
            return;
        }

        final var resource = requestedResourceService.get(resourceId.get());
        final var subscribers = resource.getSubscribers() != null
                ? new ArrayList<>(resource.getSubscribers()) : new ArrayList<URI>();

        if (!subscribers.isEmpty()) {
            new Thread(new SubscriberNotificationRunner(resource.getId(), subscribers)).start();
        }
    }

}
