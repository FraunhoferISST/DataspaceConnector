package de.fraunhofer.isst.dataspaceconnector.services.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Contains utility methods for creating ResponseEntities with different status codes and custom messages or exceptions.
 */
public class ControllerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerUtils.class);

    /**
     * Creates a ResponseEntity with status code 401 and a message indicating an invalid DAT token.
     * @param url the URL that was called.
     * @return ResponseEntity with status code 401.
     */
    public static ResponseEntity<String> respondRejectUnauthorized(String url) {
        LOGGER.debug("Unauthorized call. No DAT token found. [url=({})]", url);
        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that an error occurred in the broker
     * communication.
     * @param exception Exception that was thrown during broker communication.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<String> respondBrokerCommunicationFailed(Exception exception) {
        LOGGER.debug("Broker communication failed. [exception=({})]", exception.getMessage());
        return new ResponseEntity<>("The communication with the broker failed.",
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that the configuration could not be
     * updated.
     * @param url the URL that was called.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<String> respondUpdateError(String url) {
        LOGGER.debug("Configuration error. Could not build current connector. [url=({})]", url);
        return new ResponseEntity<>("Configuration error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 404 and a message indicating that the no configuration could be found.
     * @return ResponseEntity with status code 404.
     */
    public static ResponseEntity<String> respondConfigurationNotFound() {
        LOGGER.info("No configuration could be found.");
        return new ResponseEntity<>("No configuration found.", HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a ResponseEntity with status code 404 and a message indicating that a resource could not be found.
     * @param resourceId ID for that no match was found.
     * @return ResponseEntity with status code 404.
     */
    public static ResponseEntity<String> respondResourceNotFound(UUID resourceId) {
        LOGGER.debug("The resource does not exist. [resourceId=({})]", resourceId);
        return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that a resource could not be loaded.
     * @param resourceId ID of the resource.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<String> respondResourceCouldNotBeLoaded(UUID resourceId) {
        LOGGER.debug("Resource not loaded. [resourceId=({})]", resourceId);
        return new ResponseEntity<>("Could not load resource.",
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
 // [exception=({})]", exception.getMessage()
