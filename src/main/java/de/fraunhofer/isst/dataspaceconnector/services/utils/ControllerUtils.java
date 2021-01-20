package de.fraunhofer.isst.dataspaceconnector.services.utils;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControllerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerUtils.class);

    /**
     * The request was unauthorized.
     */
    public static ResponseEntity<String> respondRejectUnauthorized(String url) {
        LOGGER.debug("Unauthorized call. No DAT token found. [url=({})]", url);
        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }

    /**
     * The broker could not be reached.
     */
    public static ResponseEntity<String> respondBrokerCommunicationFailed(Exception exception) {
        LOGGER.debug("Broker communication failed. [exception=({})]", exception.getMessage());
        return new ResponseEntity<>("The communication with the broker failed.",
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * If the configuration/connector could not be updated.
     */
    public static ResponseEntity<String> respondUpdateError(String url) {
        LOGGER.debug("Configuration error. Could not build current connector. [url=({})]", url);
        return new ResponseEntity<>("Configuration error.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * If the configuration could not be loaded.
     */
    public static ResponseEntity<String> respondConfigurationNotFound() {
        LOGGER.info("No configuration could be found.");
        return new ResponseEntity<>("No configuration found.", HttpStatus.NOT_FOUND);
    }

    /**
     * If the resource could not be found, reject and inform the requester.
     */
    public static ResponseEntity<String> respondResourceNotFound(UUID resourceId) {
        LOGGER.debug("The resource does not exist. [resourceId=({})]", resourceId);
        return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
    }

    /**
     * An (implementation) error occurred while receiving the resource.
     */
    public static ResponseEntity<String> respondResourceCouldNotBeLoaded(UUID resourceId) {
        LOGGER.debug("Resource not loaded. [resourceId=({})]", resourceId);
        return new ResponseEntity<>("Could not load resource.",
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
 // [exception=({})]", exception.getMessage()
