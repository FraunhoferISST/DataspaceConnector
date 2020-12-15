package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.services.communication.ArtifactRequestMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.communication.DescriptionRequestMessageService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class provides endpoints for the communication with an IDS connector instance.
 */
@RestController
@RequestMapping("/admin/api/request")
@Tag(name = "Connector: IDS Connector Communication",
    description = "Endpoints for invoking external connector requests")
public class RequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

    private final TokenProvider tokenProvider;
    private final ArtifactRequestMessageService artifactRequestMessageService;
    private final DescriptionRequestMessageService descriptionRequestMessageService;

    /**
     * Constructor for RequestController
     *
     * @throws IllegalArgumentException - if any of the parameters is null.
     */
    @Autowired
    public RequestController(TokenProvider tokenProvider,
        ArtifactRequestMessageService artifactRequestMessageService,
        DescriptionRequestMessageService descriptionRequestMessageService)
        throws IllegalArgumentException {
        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (artifactRequestMessageService == null)
            throw new IllegalArgumentException("The ArtifactRequestMessageService cannot be null.");

        if (descriptionRequestMessageService == null)
            throw new IllegalArgumentException("The DescriptionRequestMessageService cannot be null.");

        this.tokenProvider = tokenProvider;
        this.artifactRequestMessageService = artifactRequestMessageService;
        this.descriptionRequestMessageService = descriptionRequestMessageService;
    }

    /**
     * Actively requests data from an external connector by building an ArtifactRequestMessage.
     *
     * @param recipient         The target connector uri.
     * @param artifactId        The requested artifact uri.
     * @param key               a {@link java.util.UUID} object.
     * @return OK or error response.
     */
    @Operation(summary = "Artifact Request",
        description = "Request data from another IDS connector. " +
            "INFO: Before an artifact can be requested, the metadata must be queried. The key" +
            " generated in this process must be passed in the artifact query.")
    @RequestMapping(value = "/artifact", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> requestData(
        @Parameter(description = "The URI of the requested IDS connector.", required = true,
            example = "https://localhost:8080/api/ids/data")
        @RequestParam("recipient") URI recipient,
        @Parameter(description = "The URI of the requested artifact.", required = true,
            example = "https://w3id.org/idsa/autogen/artifact/a4212311-86e4-40b3-ace3-ef29cd687cf9")
        @RequestParam(value = "requestedArtifact") URI artifactId,
        @Parameter(description = "A unique validation key.", required = true)
        @RequestParam("key") UUID key) {
        if (tokenProvider.getTokenJWS() != null) {
            if (resourceExists(key)) {
                try {
                    artifactRequestMessageService.setParameter(recipient, artifactId, null);
                    // Get the resource
                    final var response = artifactRequestMessageService.sendMessage(
                        artifactRequestMessageService, "");

                    if (response != null) {
                        try {
                            final var responseAsString = response.body().string();

                            try {
                                artifactRequestMessageService.saveData(responseAsString, key);
                            } catch (Exception exception) {
                                LOGGER.warn("Could not save data to database. [exception=({})]",
                                    exception.getMessage());
                                return new ResponseEntity<>("Failed to save to database.",
                                    HttpStatus.INTERNAL_SERVER_ERROR);
                            }

                            return new ResponseEntity<>(String.format("Saved at: %s \nResponse: " +
                                "%s", key, responseAsString), HttpStatus.OK);

                        } catch (NullPointerException exception) {
                            // The database response body is null.
                            LOGGER.warn("Could not read response body. [exception=({})]",
                                exception.getMessage());
                            return new ResponseEntity<>("Failed to parse database response.",
                                HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        // The response is null
                        LOGGER.warn("Received no response message.");
                        return new ResponseEntity<>("Received no response.",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } catch (IOException exception) {
                    // Failed to send a description request message
                    LOGGER.warn("Could not connect to request message service. [exception=({})]",
                        exception.getMessage());
                    return new ResponseEntity<>("Failed to reach database.",
                        HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                // The resource does not exist
                LOGGER.debug("Failed data request due to invalid key.");
                return new ResponseEntity<>("Your key is not valid. Please request metadata first.",
                    HttpStatus.FORBIDDEN);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(recipient, artifactId);
        }
    }

    /**
     * Actively requests metadata from an external connector by building an ArtifactRequestMessage.
     *
     * @param recipient         The target connector uri.
     * @param resourceId        The requested resource uri.
     * @return OK or error response.
     */
    @Operation(summary = "Description Request",
        description = "Request metadata from another IDS connector.")
    @RequestMapping(value = "/description", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> requestMetadata(
        @Parameter(description = "The URI of the requested IDS connector.", required = true,
            example = "https://localhost:8080/api/ids/data")
        @RequestParam("recipient") URI recipient,
        @Parameter(description = "The URI of the requested resource.", required = false,
            example = "https://w3id.org/idsa/autogen/resource/a4212311-86e4-40b3-ace3-ef29cd687cf9")
        @RequestParam(value = "requestedArtifact", required = false) URI resourceId) {
        if (tokenProvider.getTokenJWS() != null) {
            try {
                descriptionRequestMessageService.setParameter(recipient, resourceId);
                final var response = descriptionRequestMessageService.sendMessage(
                    descriptionRequestMessageService, "");

                if (response != null) {
                    try {
                        final var responseAsString = response.body().string();

                        if (resourceId != null) {
                            // Save the artifact request
                            try {
                                final var validationKey =
                                    descriptionRequestMessageService.saveMetadata(responseAsString);
                                return new ResponseEntity<>(
                                    "Validation: " + validationKey + "\n" + responseAsString,
                                    HttpStatus.OK);
                            } catch (Exception e) {
                                LOGGER.error("Caught unhandled exception. [exception=({})]",
                                    e.getMessage());
                                return new ResponseEntity<>(e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        } else {
                            // Send self description
                            return new ResponseEntity<>(responseAsString, HttpStatus.OK);
                        }
                    } catch (NullPointerException exception) {
                        // The database response body is null.
                        LOGGER.warn("Could not read response body. [exception=({})]",
                            exception.getMessage());
                        return new ResponseEntity<>("Failed to parse database response.",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    // The response is null
                    LOGGER.warn("Received no response message.");
                    return new ResponseEntity<>("Received no response.",
                        HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (IOException exception) {
                // Failed to send description request message
                LOGGER.warn("Could not connect to request message service. [exception=({})]",
                    exception.getMessage());
                return new ResponseEntity<>("Failed to send description request message.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(recipient, resourceId);
        }
    }

    private ResponseEntity<String> respondRejectUnauthorized(URI recipient, URI requestedArtifact) {
        // The request was unauthorized.
        LOGGER
            .debug(
                "Unauthorized call. No DAT token found. [recipient=({}), requestedArtifact=({})]",
                recipient.toString(), requestedArtifact.toString());

        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Checks if a resource exists.
     *
     * @param resourceId The resource uuid.
     * @return true if the resource exists.
     */
    private boolean resourceExists(UUID resourceId) {
        try {
            return true;
//            return resourceService.getResource(resourceId) != null;
        } catch (ResourceException exception) {
            return false;
        }
    }
}
