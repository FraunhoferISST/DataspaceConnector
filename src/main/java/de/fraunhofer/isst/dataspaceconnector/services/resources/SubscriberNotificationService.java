package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.controller.ResourceSubscriptionController;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.repositories.RequestedResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 *
 */
@Service
public class SubscriberNotificationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberNotificationService.class);
    
    private final RequestedResourceRepository requestedResourceRepository;
    
    @Autowired
    public SubscriberNotificationService(RequestedResourceRepository requestedResourceRepository) {
        this.requestedResourceRepository = requestedResourceRepository;
    }
    
    /**
     * @param uuid
     * @param data
     * @return
     */
    public ResponseEntity<String> subscribeUrl(UUID uuid, String data) {
        RequestedResource requestedResource = null;
        try {
            requestedResource = requestedResourceRepository.getOne(uuid);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }
    
        try {
            if (requestedResource.getSubscribers().contains(new URI(data))) {
                return new ResponseEntity<>("The URL is already subscribed to the given resource.", HttpStatus.OK);
            } else {
                requestedResource.getSubscribers().add(new URI(data));
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
        RequestedResource requestedResource = null;
        try {
            requestedResource = requestedResourceRepository.getOne(uuid);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }
        
        try {
            requestedResource.getSubscribers().remove(new URI(data));
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>("An internal error occurred.", HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>("The URL was deleted from the subscribers list of the given resource ID.", HttpStatus.OK);
    }
    
    /**
     * @param uuid
     * @param data
     * @return
     */
    public ResponseEntity<String> updateSubscribedUrl(UUID uuid, String data) {
        RequestedResource requestedResource = null;
        try {
            requestedResource = requestedResourceRepository.getOne(uuid);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>("Could not found resource for given ID.", HttpStatus.NOT_FOUND);
        }
        
        try {
            requestedResource.getSubscribers().add(new URI(data));
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>("An internal error occurred.", HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>("The URL was updated in the subscribers list of the given resource ID.", HttpStatus.OK);
    }
}
