package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestServiceUtils;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import io.jsonwebtoken.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * This class provides endpoints for the communication with an IDS connector instance.
 *
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api/request")
@Tag(name = "Connector: IDS Connector Communication",
        description = "Endpoints for invoking external connector requests")
public class RequestController {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

    private final TokenProvider tokenProvider;
    private final ConnectorRequestServiceImpl requestMessageService;
    private final ConnectorRequestServiceUtils connectorRequestServiceUtils;

    /**
     * <p>Constructor for RequestController.</p>
     *
     * @param tokenProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider} object.
     * @param requestMessageService a {@link ConnectorRequestServiceImpl} object.
     * @param connectorRequestServiceUtils a {@link ConnectorRequestServiceUtils} object.
     */
    @Autowired
    public RequestController(@NotNull TokenProvider tokenProvider,
                             @NotNull ConnectorRequestServiceImpl requestMessageService,
                             @NotNull ConnectorRequestServiceUtils connectorRequestServiceUtils) {
        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (requestMessageService == null)
            throw new IllegalArgumentException("The ConnectorRequestService cannot be null.");

        if (connectorRequestServiceUtils == null)
            throw new IllegalArgumentException("The ConnectorRequestServiceUtils cannot be null.");

        this.tokenProvider = tokenProvider;
        this.requestMessageService = requestMessageService;
        this.connectorRequestServiceUtils = connectorRequestServiceUtils;
    }

    /**
     * Actively requests data from an external connector by building an ArtifactRequestMessage.
     *
     * @param recipient         The target connector uri.
     * @param requestedArtifact The requested resource uri.
     * @return OK or error response.
     * @param key a {@link java.util.UUID} object.
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
            @RequestParam(value = "requestedArtifact") URI requestedArtifact,
            @Parameter(description = "A unique validation key.", required = true)
            @RequestParam("key") UUID key) {
        Assert.notNull(tokenProvider, "The tokenProvider cannot be null.");
        Assert.notNull(connectorRequestServiceUtils, "The connectorRequestServiceUtils cannot be null.");
        Assert.notNull(requestMessageService, "The requestMessageService cannot be null.");

        if (tokenProvider.getTokenJWS() != null) {
            if (connectorRequestServiceUtils.resourceExists(key)) {
                try {
                    // Get the resource
                    final var response =
                            requestMessageService.sendArtifactRequestMessage(recipient,
                                    requestedArtifact);

                    if (response != null) {
                        try {
                            final var responseAsString = response.body().string();

                            try {
                                connectorRequestServiceUtils.saveData(responseAsString, key);
                            } catch (Exception exception) {
                                LOGGER.error("Could not save data to database.", exception);
                                return new ResponseEntity<>("Failed to save to database.",
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                            }

                            return new ResponseEntity<>(String.format("Saved at: %s \nResponse: " +
                                            "%s", key, responseAsString), HttpStatus.OK);

                        } catch (NullPointerException exception) {
                            // The database response body is null.
                            LOGGER.error("Could not read response body.", exception);
                            return new ResponseEntity<>("Failed to parse database response.",
                                    HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        // The response is null
                        LOGGER.warn("Received no response message.");
                        return new ResponseEntity<>("Received no response.",
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }catch(IOException exception){
                    // Failed to send a description request message
                    LOGGER.info("Could not connect to request message service.");
                    return new ResponseEntity<>("Failed to reach to database.",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                // The resource does not exist
                LOGGER.warn(String.format("Failed data request due to invalid key.\nRecipient: " +
                        "%s\nrequestedArtifact:%s\nkey:%s", recipient.toString(),
                        requestedArtifact.toString(), key.toString()));

                return new ResponseEntity<>("Your key is not valid. Please request metadata first.",
                        HttpStatus.FORBIDDEN);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(recipient, requestedArtifact);
        }
    }

    /**
     * Actively requests metadata from an external connector by building an ArtifactRequestMessage.
     *
     * @param recipient         The target connector uri.
     * @param requestedArtifact The requested resource uri.
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
            @RequestParam(value = "requestedArtifact", required = false) URI requestedArtifact) {
        if (tokenProvider.getTokenJWS() != null) {
            try {
                final var response = requestMessageService.sendDescriptionRequestMessage(
                        recipient, requestedArtifact);

                if(response != null) {
                    try {
                        final var responseAsString = response.body().string();

                        if (requestedArtifact != null) {
                            // Save the artifact request
                            try {
                                final var validationKey =
                                        connectorRequestServiceUtils.saveMetadata(responseAsString);
                                return new ResponseEntity<>("Validation: " + validationKey + "\n" + responseAsString,
                                        HttpStatus.OK);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage());
                                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        }else{
                            // Send self description
                            return new ResponseEntity<>(responseAsString, HttpStatus.OK);
                        }
                    } catch (NullPointerException exception) {
                        // The database response body is null.
                        LOGGER.error("Could not read response body.", exception);
                        return new ResponseEntity<>("Failed to parse database response.",
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }else {
                    // The response is null
                    LOGGER.warn("Received no response message.");
                    return new ResponseEntity<>("Received no response.",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }catch(IOException exception) {
                // Failed to send description request message
                LOGGER.info("Could not connect to request message service.");
                return new ResponseEntity<>("Failed to send description request message.",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // The request was unauthorized.
            return respondRejectUnauthorized(recipient, requestedArtifact);
        }
    }

    private ResponseEntity<String> respondRejectUnauthorized(URI recipient, URI requestedArtifact) {
        // The request was unauthorized.
        LOGGER.warn(String.format("Unauthorized call. No DAT token found. Tried call with " +
                "recipient %s and requestedArtifact %s.",
                recipient.toString(), requestedArtifact.toString()));

        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }
}
