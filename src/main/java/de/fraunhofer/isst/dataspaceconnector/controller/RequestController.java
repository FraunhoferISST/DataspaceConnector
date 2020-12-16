package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.services.communication.MessageResponseService.ResponseType;
import de.fraunhofer.isst.dataspaceconnector.services.communication.request.ArtifactRequestMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.communication.request.DescriptionRequestMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.communication.response.ArtifactResponseMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.communication.response.DescriptionResponseMessageService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import okhttp3.Response;
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
    private final ArtifactResponseMessageService artifactResponseMessageService;
    private final DescriptionResponseMessageService descriptionResponseMessageService;

    private Response response;
    private String responseAsString;

    /**
     * Constructor for RequestController
     *
     * @throws IllegalArgumentException - if any of the parameters is null.
     */
    @Autowired
    public RequestController(TokenProvider tokenProvider,
        ArtifactRequestMessageService artifactRequestMessageService,
        DescriptionRequestMessageService descriptionRequestMessageService,
        ArtifactResponseMessageService artifactResponseMessageService,
        DescriptionResponseMessageService descriptionResponseMessageService)
        throws IllegalArgumentException {
        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (artifactRequestMessageService == null)
            throw new IllegalArgumentException("The ArtifactRequestMessageService cannot be null.");

        if (descriptionRequestMessageService == null)
            throw new IllegalArgumentException("The DescriptionRequestMessageService cannot be null.");

        if (artifactResponseMessageService == null)
            throw new IllegalArgumentException("The ArtifactResponseMessageService cannot be null.");

        if (descriptionResponseMessageService == null)
            throw new IllegalArgumentException("The DescriptionResponseMessageService cannot be null.");

        this.tokenProvider = tokenProvider;
        this.artifactRequestMessageService = artifactRequestMessageService;
        this.descriptionRequestMessageService = descriptionRequestMessageService;
        this.artifactResponseMessageService = artifactResponseMessageService;
        this.descriptionResponseMessageService = descriptionResponseMessageService;
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
        if (tokenProvider.getTokenJWS() == null) {
            // The request was unauthorized.
            return respondRejectUnauthorized(recipient, artifactId);
        }

        if (!resourceExists(key)) {
            // The resource does not exist
            LOGGER.warn(String.format("Failed data request due to invalid key.\nRecipient: " +
                    "%s\nrequestedArtifact:%s\nkey:%s", recipient.toString(),
                artifactId.toString(), key.toString()));

            return new ResponseEntity<>("Your key is not valid. Please request metadata first.",
                HttpStatus.FORBIDDEN);
        }

        try {
            // Send ArtifactRequestMessage.
            artifactRequestMessageService.setParameter(recipient, artifactId, null);
            response = artifactRequestMessageService.sendMessage(artifactRequestMessageService, "");
        } catch (MessageException exception) {
            // Failed to send a description request message
            LOGGER.info("Could not connect to request message service.");
            return new ResponseEntity<>("Failed to send an ids message.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<ResponseType, String> map;
        try {
            map = artifactResponseMessageService.readResponse(response);
        } catch (MessageResponseException exception) {
            LOGGER.warn("Could not read response body. [exception=({})]",
                exception.getMessage());
            return new ResponseEntity<>("Failed to parse response.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final var payload = map.get(ResponseType.ARTIFACT_RESPONSE);
        if (payload == null) {
            return returnRejectionMessage(map);
        }

        try {
            // Save data to database.
            artifactResponseMessageService.saveData(payload, key);
            return new ResponseEntity<>(String.format("Saved at: %s \nResponse: " +
                "%s", key, payload), HttpStatus.OK);
        } catch (Exception exception) {
            LOGGER.warn("Could not save data to database. [exception=({})]",
                exception.getMessage());
            return new ResponseEntity<>("Failed to save to database.",
                HttpStatus.INTERNAL_SERVER_ERROR);
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
        @RequestParam(value = "requestedResource", required = false) URI resourceId) {
        if (tokenProvider.getTokenJWS() == null) {
            // The request was unauthorized.
            return respondRejectUnauthorized(recipient, resourceId);
        }

        try {
            // Send DescriptionRequestMessage.
            descriptionRequestMessageService.setParameter(recipient, resourceId);
            response = descriptionRequestMessageService.sendMessage(descriptionRequestMessageService, "");
        } catch (MessageException exception) {
            // Failed to send description request message.
            LOGGER.info("Could not connect to request message service. " + exception.getMessage());
            return new ResponseEntity<>("Failed to send description request message.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<ResponseType, String> map;
        try {
            map = descriptionResponseMessageService.readResponse(response);
        } catch (MessageResponseException exception) {
            LOGGER.warn("Could not read response body. [exception=({})]",
                exception.getMessage());
            return new ResponseEntity<>("Failed to parse response.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final var payload = map.get(ResponseType.DESCRIPTION_RESPONSE);
        if (payload == null) {
            return returnRejectionMessage(map);
        }

        if (resourceId != null) {
            // Save metadata to database.
            try {
                final var validationKey = descriptionResponseMessageService.saveMetadata(payload, resourceId);
                return new ResponseEntity<>("Validation: " + validationKey +
                    "\n" + payload, HttpStatus.OK);
            } catch (Exception e) {
                LOGGER.error("Caught unhandled exception. [exception=({})]",
                    e.getMessage());
                return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // Return self-description.
            return new ResponseEntity<>(map.get(ResponseType.DESCRIPTION_RESPONSE), HttpStatus.OK);
        }
    }

    private ResponseEntity<String> respondRejectUnauthorized(URI recipient, URI requestedArtifact) {
        // The request was unauthorized.
        LOGGER.debug(
                "Unauthorized call. No DAT token found. [recipient=({}), requestedArtifact=({})]",
                recipient.toString(), requestedArtifact.toString());

        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<String> returnRejectionMessage(Map<ResponseType, String> map) {
        if (map.get(ResponseType.REJECTION) != null) {
            return new ResponseEntity<>("Rejection Message: "
                + map.get(ResponseType.REJECTION), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unexpected response: \n" + map,
                HttpStatus.OK);
        }
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
