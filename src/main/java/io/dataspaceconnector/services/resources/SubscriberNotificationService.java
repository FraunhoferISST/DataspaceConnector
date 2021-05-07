package io.dataspaceconnector.services.resources;

import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.repositories.RequestedResourcesRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * This class provides methods for handling subscriptions to a requested resource.
 */
@Log4j2
@Service
public class SubscriberNotificationService {

    private final RequestedResourcesRepository requestedResourcesRepository;

    @Autowired
    public SubscriberNotificationService(final RequestedResourcesRepository requestedResourcesRepository) {
        this.requestedResourcesRepository = requestedResourcesRepository;
    }

    /**
     * Add a url to the subscribers of a given resource with uuid.
     *
     * @param uuid the uuid of the resource.
     * @param uri the url.
     */
    public ResponseEntity<String> subscribeUrl(final UUID uuid, final URI uri) {
        Optional<RequestedResource> requestedResource;
        try {
            requestedResource = requestedResourcesRepository.findById(uuid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }

        if (requestedResource.isEmpty()){
            log.error("Could not found resource for given ID.");
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }



        if (requestedResource.get().getSubscribers().contains(uri)) {
            return new ResponseEntity<>("The URL is already subscribed to the given resource.", HttpStatus.OK);
        } else {
            requestedResource.get().getSubscribers().add(uri);

            requestedResourcesRepository.save(requestedResource.get());
        }


        return new ResponseEntity<>("The URL was subscribed to the given resource ID.", HttpStatus.OK);
    }

    /**
     * Removes a url from the subscribers of a given resource with uuid.
     *
     * @param uuid the uuid of the resource.
     * @param uri the url.
     */
    public ResponseEntity<String> deleteSubscribedUrl(UUID uuid, URI uri) {
        Optional<RequestedResource> requestedResource;
        try {
            requestedResource = requestedResourcesRepository.findById(uuid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }

        if (requestedResource.isEmpty()){
            log.error("Could not found resource for given ID.");
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }


        if (requestedResource.get().getSubscribers().contains(uri)) {
            requestedResource.get().getSubscribers().remove(uri);

            requestedResourcesRepository.save(requestedResource.get());
        }


        return new ResponseEntity<>("The URL was deleted from the subscribers list of the given resource ID.", HttpStatus.OK);
    }

    /**
     * Notifies all backend systems subscribed for updates to a requested resource. The backends
     * are notified in parallel and asynchronously. If a request to one of the subscribed URLs
     * results in a status code 5xx, the request is retried 5 times with a delay of 5 seconds each.
     *
     * @param resource the requested resource that was updated.
     */
    public void notifySubscribers(final RequestedResource resource) {
        ArrayList<URI> subscribers = new ArrayList<>(resource.getSubscribers());

        Thread notifyThread = new Thread(new Runnable() {
            private static final long numberOfRetries = 5;
            private static final long secondsBetweenRetries = 5;

            private final WebClient webClient = WebClient.create();

            /**
             * Sends the given resource ID to the given URL as the body of a POST request. If the
             * response has a status code 5xx, the request is retried 5 times with a delay of 5
             * seconds each.
             *
             * @param uri the recipient
             * @param resourceUUID the resource ID
             * @return a {@link Mono} with the response body as string
             */
            public Mono<String> notifySubscriber(final URI uri, final UUID resourceUUID) {
                return webClient
                        .post()
                        .uri(uri)
                        .bodyValue(resourceUUID)
                        .retrieve().bodyToMono(String.class)
                        .retryWhen(Retry
                                .fixedDelay(numberOfRetries, Duration.ofSeconds(secondsBetweenRetries))
                                .filter(this::shouldRetry))
                        .doOnError(throwable -> log.error("Could not notify subscriber at: {}", uri));
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
                    return ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
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
                        .flatMap(uri ->
                                notifySubscriber(uri, resource.getId())
                        ).subscribe();
            }
        });
        notifyThread.start();
    }

}
