package de.fraunhofer.isst.dataspaceconnector.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.ws.rs.ProcessingException;
import java.net.URI;
import java.util.LinkedList;

/**
 * The Service for updating internal Resources by fetching external Resources after receiving a ResourceUpdateMessage
 */
@Service
@EnableScheduling
public class ResourceUpdateService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceUpdateService.class);

    private final LinkedList<URI> resourceUpdateList = new LinkedList<>();

    /**
     * Constructor
     */
    @Autowired
    public ResourceUpdateService() {    }

    /**
     * Add a URI to be processed
     *
     * @param affectedResource the URI of the affected Resource
     */
    private void addItemToQueue(URI affectedResource) {
        resourceUpdateList.offer(affectedResource);
    }

    /**
     * Updates the affected Resource if possible, otherwise adds it to the processing queue
     *
     * @param affectedResource the resource which needs to be updated
     * @return true iff resource was updated, false iff if could not been updated
     */
    public boolean updateOrSchedule(URI affectedResource) {
        try{
            updateResource(affectedResource);
            return true;
        } catch (ProcessingException e) {
            addItemToQueue(affectedResource);
            return false;
        }
    }

    /**
     * Update a single Resource
     *
     * @param affectedResource The Resource to be updated
     * @throws ProcessingException if updating did not succeed
     */
    private void updateResource(URI affectedResource) throws ProcessingException {
        try {
            LOGGER.info("resource {} updated.", affectedResource.toString());
            // TODO: pull new representation of resources
        } catch (Exception e) {
            throw new ProcessingException("Message");
        }
    }

    /**
     * Scheduled Method to update items of the queue
     */
    @Scheduled(fixedRateString = "${scheduling.update:100}")
    public void processQueue() {
        if(!resourceUpdateList.isEmpty()) {
            try{
                updateResource(resourceUpdateList.peek());

                // Resource has been handled successfully
                resourceUpdateList.pop();
            } catch (ProcessingException e) { // Processing Error, add resource at end of queue
                resourceUpdateList.offer(resourceUpdateList.pop());
            }
        }
    }
}
