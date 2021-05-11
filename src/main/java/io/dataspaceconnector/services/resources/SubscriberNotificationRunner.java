package io.dataspaceconnector.services.resources;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

/**
 * A Runnable that asynchronously sends notifications with the ID of an updated requested resource
 * to the list of subscribed URLs.
 */
@Log4j2
@RequiredArgsConstructor
public class SubscriberNotificationRunner implements Runnable {

    /** Maximum number of retries when a notification fails. */
    private static final long MAX_RETRIES = 5;

    /** Time between retries in seconds. */
    private static final long RETRY_DELAY = 5;

    /**
     * ID of the resource that was updated.
     */
    private final @NonNull UUID resourceId;

    /**
     * List of URLs subscribed for updates to the resource.
     */
    private final @NonNull List<URI> subscribers;

    /** The {@link WebClient} to use for sending the notifications. */
    private final WebClient webClient = WebClient.create();

    /**
     * Sends a notification to each URL subscribed for updates to a given resource in parallel.
     */
    @Override
    public void run() {
        Flux.fromIterable(subscribers)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(this::notifySubscriber)
                .subscribe();
    }

    /**
     * Sends the given resource ID to the given URL as the body of a POST request. If the
     * response has a status code 5xx, the request is retried 5 times with a delay of 5
     * seconds each.
     *
     * @param uri the recipient
     * @return a {@link Mono} with the response body as string
     */
    private Mono<String> notifySubscriber(final URI uri) {
        return webClient
                .post()
                .uri(uri)
                .bodyValue(resourceId)
                .retrieve().bodyToMono(String.class)
                .retryWhen(Retry
                        .fixedDelay(MAX_RETRIES, Duration.ofSeconds(RETRY_DELAY))
                        .filter(this::shouldRetry))
                .onErrorResume(throwable -> {
                    log.error("Could not notify subscriber at: {}", uri);
                    return Mono.just("Could not notify subscriber at: " + uri);
                });
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

}
