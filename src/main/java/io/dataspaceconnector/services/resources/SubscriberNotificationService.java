package io.dataspaceconnector.services.resources;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

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
        final var requestedResource = requestedResourceService.get(resourceId);

        var subscribers = requestedResource.getSubscribers();
        if (subscribers == null) {
            subscribers = new ArrayList<>();
        }

        if (subscribers.contains(uri)) {
            subscribers.remove(uri);
            requestedResourceService.updateSubscriptions(resourceId, subscribers);
        }
    }

    /**
     * Notifies all backend systems subscribed for updates to a requested resource. The backends
     * are notified in parallel and asynchronously. If a request to one of the subscribed URLs
     * results in a status code 5xx, the request is retried 5 times with a delay of 5 seconds each.
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

        final var subscribers = new ArrayList<>(resource.getSubscribers());

        final var notifyThread = new Thread(new Runnable() {
            /** Maximum number of retries when a notification fails. */
            private static final long MAX_RETRIES = 5;
            /** Time between retries in seconds. */
            private static final long RETRY_DELAY = 5;
            /** The WebClient to use  */
            private final WebClient webClient = WebClient.create();

            /**
             * Sends the given resource ID to the given URL as the body of a POST request. If the
             * response has a status code 5xx, the request is retried 5 times with a delay of 5
             * seconds each.
             *
             * @param uri the recipient
             * @param resourceId the resource ID
             * @return a {@link Mono} with the response body as string
             */
            private Mono<String> notifySubscriber(final URI uri, final UUID resourceId) {
                return webClient
                        .post()
                        .uri(uri)
                        .bodyValue(resourceId)
                        .retrieve().bodyToMono(String.class)
                        .retryWhen(Retry
                                .fixedDelay(MAX_RETRIES, Duration.ofSeconds(RETRY_DELAY))
                                .filter(this::shouldRetry))
                        .doOnError(throwable
                                -> log.error("Could not notify subscriber at: {}", uri));
            }

            /**
             * Checks whether a request should be retried based on the exception thrown by the
             * {@link WebClient}. The request should be retried if a status code 5xx was returned.
             *
             * @param throwable the exception thrown by the WebClient
             * @return true, if the request should be retried; false otherwise
             */
            private boolean shouldRetry(final Throwable throwable) {
                if (throwable instanceof WebClientResponseException) {
                    return ((WebClientResponseException) throwable)
                            .getStatusCode()
                            .is5xxServerError();
                }
                return false;
            }

            /**
             * Sends a request to each URL subscribed for updates to a given resource in parallel.
             */
            @Override
            public void run() {
                Flux.fromIterable(subscribers)
                        .parallel()
                        .runOn(Schedulers.boundedElastic())
                        .flatMap(uri -> notifySubscriber(uri, resource.getId())
                        ).subscribe();
            }
        });
        notifyThread.start();
    }

}
