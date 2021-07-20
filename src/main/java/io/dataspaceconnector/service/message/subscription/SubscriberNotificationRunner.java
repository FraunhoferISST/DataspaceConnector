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

import io.dataspaceconnector.controller.util.Notification;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.List;

/**
 * A Runnable that asynchronously sends notifications with the ID of an updated requested resource
 * to the list of subscribed URLs.
 */
@Log4j2
@RequiredArgsConstructor
public class SubscriberNotificationRunner implements Runnable {

    /**
     * Maximum number of retries when a notification fails.
     */
    private static final long MAX_RETRIES = 5;

    /**
     * Time between retries in seconds.
     */
    private static final long RETRY_DELAY = 5;

    /**
     * Notification object that should be sent.
     */
    private final @NonNull Notification notification;

    /**
     * List of URLs subscribed for updates to the resource.
     */
    private final @NonNull List<URI> recipients;

    /**
     * The artifact's data.
     */
    private final @NonNull InputStream data;

    /**
     * The {@link WebClient} to use for sending the notifications.
     */
    private final WebClient webClient = WebClient.create();

    /**
     * Sends a notification to each URL subscribed for updates to a given resource in parallel.
     */
    @Override
    public void run() {
        Flux.fromIterable(recipients)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(this::notifySubscriber)
                .subscribe();
    }

    /**
     * Sends the given notification to the given URL as the body of a POST request. If the response
     * has a status code 5xx, the request is retried 5 times with a delay of 5 seconds each.
     * Header params contain target id and event, the body contains the data.
     *
     * @param uri the recipient
     * @return a {@link Mono} with the response body as string
     */
    private Mono<String> notifySubscriber(final URI uri) {
        return webClient
                .post()
                .uri(uri)
                .header("ids:target", notification.getTarget().toString())
                .header("ids:event", notification.getEvent().toString())
                .body(Mono.just(data), InputStream.class)
                .retrieve().bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(MAX_RETRIES, Duration.ofSeconds(RETRY_DELAY))
                        .filter(this::shouldRetry))
                .onErrorResume(throwable -> {
                    if (log.isWarnEnabled()) {
                        log.warn("Could not notify subscriber. [url=({})]", uri);
                    }
                    return Mono.just(String.format("Could not notify subscriber at %s.", uri));
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
            return ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
        }
        return false;
    }

}
