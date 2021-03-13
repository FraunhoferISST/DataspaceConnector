package de.fraunhofer.isst.dataspaceconnector.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Contains utility methods for creating ResponseEntities with different status codes and custom
 * messages or exceptions.
 */
public final class ControllerUtils {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerUtils.class);

    /**
     * Creates a ResponseEntity with status code 401 and a message indicating an invalid DAT token.
     *
     * @param url the URL that was called.
     * @return ResponseEntity with status code 401.
     */
    public static ResponseEntity<Object> respondRejectUnauthorized(final String url) {
        LOGGER.debug("Unauthorized call. No DAT token found. [url=({})]", url);
        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that an error occurred
     * in the broker communication.
     *
     * @param exception Exception that was thrown during broker communication.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> respondBrokerCommunicationFailed(final Exception exception) {
        LOGGER.debug("Broker communication failed. [exception=({})]", exception.getMessage());
        return new ResponseEntity<>("The communication with the broker failed.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<Object> responseFailedToLoadSerializer(final Exception exception) {
        LOGGER.warn("Failed to receive the serializer. [exception=({})]", exception.getMessage());
        return new ResponseEntity<>("Failed to update configuration.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that the configuration
     * could not be updated.
     *
     * @param exception The exception that was thrown.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> respondConfigurationUpdateError(final Exception exception) {
        LOGGER.debug("Failed to update the configuration. [exception=({})]",
                exception.getMessage());
        return new ResponseEntity<>("Failed to update configuration.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that an input could
     * not be deserialized.
     *
     * @param exception The exception that was thrown.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> responseDeserializationError(final Exception exception) {
        LOGGER.warn("Failed to deserialize the object. [exception=({})]", exception.getMessage());
        return new ResponseEntity<>("Failed to update.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 404 and a message indicating that the no
     * configuration could be found.
     *
     * @return ResponseEntity with status code 404.
     */
    public static ResponseEntity<Object> respondConfigurationNotFound() {
        LOGGER.info("No configuration could be found.");
        return new ResponseEntity<>("No configuration found.", HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a ResponseEntity with status code 404 and a message indicating that a resource
     * could not be found.
     *
     * @param resourceId ID for that no match was found.
     * @return ResponseEntity with status code 404.
     */
    public static ResponseEntity<Object> respondResourceNotFound(final UUID resourceId) {
        LOGGER.debug("The resource does not exist. [resourceId=({})]", resourceId);
        return new ResponseEntity<>("Resource not found.", HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that a resource
     * could not be loaded.
     *
     * @param resourceId ID of the resource.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> respondResourceCouldNotBeLoaded(final UUID resourceId) {
        LOGGER.debug("Resource not loaded. [resourceId=({})]", resourceId);
        return new ResponseEntity<>("Could not load resource.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that no predefined
     * policy pattern has been recognized.
     *
     * @param exception The exception that was thrown.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> responsePatternNotIdentified(final Exception exception) {
        LOGGER.debug("Failed to identify policy pattern.", exception);
        return new ResponseEntity<>("Could not identify pattern", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a ResponseEntity with status code 500 and a message indicating that the input was
     * invalid.
     *
     * @param exception The exception that was thrown.
     * @return ResponseEntity with status code 500.
     */
    public static ResponseEntity<Object> responseInvalidInput(final Exception exception) {
        LOGGER.warn("Failed to deserialize the input. [exception=({})]", exception.getMessage());
        return new ResponseEntity<>("Invalid input.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
// [exception=({})]", exception.getMessage()
