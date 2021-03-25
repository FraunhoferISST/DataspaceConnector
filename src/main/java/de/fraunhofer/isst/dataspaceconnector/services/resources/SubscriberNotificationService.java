package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.controller.ResourceSubscriptionController;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.repositories.OfferedResourceRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.RequestedResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 *
 */
@Service
public class SubscriberNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberNotificationService.class);

    private final RequestedResourceRepository requestedResourceRepository;

    @Autowired
    public SubscriberNotificationService( RequestedResourceRepository requestedResourceRepository) {
        this.requestedResourceRepository = requestedResourceRepository;
    }

    /**
     * @param uuid
     * @param data
     * @return
     */
    public ResponseEntity<String> subscribeUrl(UUID uuid, String data) {
        Optional<RequestedResource> requestedResource = null;
        try {
            requestedResource = requestedResourceRepository.findById(uuid);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }

        if(requestedResource.isEmpty()){
            LOGGER.error("Could not found resource for given ID.");
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }

        try {

            if (requestedResource.get().getSubscribers().contains(new URI(data))) {
                return new ResponseEntity<>("The URL is already subscribed to the given resource.", HttpStatus.OK);
            } else {
                requestedResource.get().getSubscribers().add(new URI(data));

                requestedResourceRepository.save(requestedResource.get());
            }
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>("An internal error occurred.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("The URL was subscribed to the given resource ID.", HttpStatus.OK);
    }

    /**
     * @param uuid
     * @param data
     * @return
     */
    public ResponseEntity<String> deleteSubscribedUrl(UUID uuid, String data) {
        Optional<RequestedResource> requestedResource = null;
        try {
            requestedResource = requestedResourceRepository.findById(uuid);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }

        if(requestedResource.isEmpty()){
            LOGGER.error("Could not found resource for given ID.");
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }

        try {
            if (requestedResource.get().getSubscribers().contains(new URI(data))) {
                requestedResource.get().getSubscribers().remove(new URI(data));

                requestedResourceRepository.save(requestedResource.get());
            }
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>("An internal error occurred.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("The URL was deleted from the subscribers list of the given resource ID.", HttpStatus.OK);
    }

    /**
     * @param resource
     */
    public void notifySubscribers(RequestedResource resource) {

        ArrayList<URI> unsuccessfulSubscribers = new ArrayList<>(resource.getSubscribers());



        Thread notifyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int loopCounter = 0;
                while (!unsuccessfulSubscribers.isEmpty()) {
                    try {
                        for (URI uri : unsuccessfulSubscribers) {
                            WebClient.create()
                                    .post()
                                    .uri(uri)
                                    .body(resource.getUuid(), String.class)
                                    .retrieve().onStatus(HttpStatus::is2xxSuccessful, response -> {
                                unsuccessfulSubscribers.remove(uri);
                                LOGGER.info("Removing subscriber {}", uri);
                                return null;
                            });
                        }

                        Thread.sleep(2000);

                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                    if(loopCounter > 5){
                        break;
                    }
                    loopCounter++;
                }

            }
        });
        notifyThread.start();

    }
}
