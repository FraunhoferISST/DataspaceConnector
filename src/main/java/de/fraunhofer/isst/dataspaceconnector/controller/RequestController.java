package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.NegotiationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.ResponseService.ResponseType;
import de.fraunhofer.isst.dataspaceconnector.services.messages.request.ArtifactRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.request.DescriptionRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.response.ArtifactResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.response.ContractResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.response.DescriptionResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

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
    private final ArtifactRequestService artifactRequestService;
    private final DescriptionRequestService descriptionRequestService;
    private final ArtifactResponseService artifactResponseService;
    private final DescriptionResponseService descriptionResponseService;
    private final ContractResponseService contractResponseService;
    private final NegotiationService negotiationService;
    private final ResourceService resourceService;

    /**
     * Constructor for RequestController
     *
     * @throws IllegalArgumentException - if any of the parameters is null.
     */
    @Autowired
    public RequestController(TokenProvider tokenProvider,
        ArtifactRequestService artifactRequestService,
        DescriptionRequestService descriptionRequestService,
        ArtifactResponseService artifactResponseService,
        DescriptionResponseService descriptionResponseService,
        ContractResponseService contractResponseService,
        NegotiationService negotiationService,
        RequestedResourceServiceImpl requestedResourceService)
        throws IllegalArgumentException {
        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (artifactRequestService == null)
            throw new IllegalArgumentException("The ArtifactRequestService cannot be null.");

        if (descriptionRequestService == null)
            throw new IllegalArgumentException("The DescriptionRequestService cannot be null.");

        if (artifactResponseService == null)
            throw new IllegalArgumentException("The ArtifactResponseService cannot be null.");

        if (descriptionResponseService == null)
            throw new IllegalArgumentException("The DescriptionResponseService cannot be null.");

        if (contractResponseService == null)
            throw new IllegalArgumentException("The ContractResponseService cannot be null.");

        if (negotiationService == null)
            throw new IllegalArgumentException("The NegotiationService cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The RequestedResourceServiceImpl cannot be null.");

        this.tokenProvider = tokenProvider;
        this.artifactRequestService = artifactRequestService;
        this.descriptionRequestService = descriptionRequestService;
        this.artifactResponseService = artifactResponseService;
        this.descriptionResponseService = descriptionResponseService;
        this.contractResponseService = contractResponseService;
        this.negotiationService = negotiationService;
        this.resourceService = requestedResourceService;
    }

    /**
     * Requests metadata from an external connector by building an ArtifactRequestMessage.
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
        @Parameter(description = "The URI of the requested resource.",
            example = "https://w3id.org/idsa/autogen/resource/a4212311-86e4-40b3-ace3-ef29cd687cf9")
        @RequestParam(value = "requestedResource", required = false) URI resourceId) {
        if (tokenProvider.getTokenJWS() == null) {
            return respondRejectUnauthorized(recipient, resourceId);
        }

        Response response;
        try {
            // Send DescriptionRequestMessage.
            descriptionRequestService.setParameter(recipient, resourceId);
            response = descriptionRequestService.sendMessage("");
        } catch (MessageException exception) {
            // Failed to send description request message.
            LOGGER.info("Could not connect to request message service. " + exception.getMessage());
            return new ResponseEntity<>("Failed to send description request message.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<ResponseType, String> map;
        try {
            map = descriptionResponseService.handleResponse(response);
        } catch (MessageResponseException exception) {
            // The response is null.
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
                final var validationKey = descriptionResponseService
                    .saveMetadata(payload, resourceId);
                return new ResponseEntity<>("Validation: " + validationKey +
                    "\nResponse: " + payload, HttpStatus.OK);
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

    /**
     * Sends a contract request to a connector by building an ContractRequestMessage.
     *
     * @return OK or error response.
     */
    @Operation(summary = "Contract Request",
        description = "Send a contract request to another IDS connector.")
    @RequestMapping(value = "/contract", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> requestContract(
        @Parameter(description = "The URI of the requested IDS connector.", required = true,
            example = "https://localhost:8080/api/ids/data")
        @RequestParam("recipient") URI recipient,
        @Parameter(description = "The URI of the requested artifact.", required = true,
            example = "https://w3id.org/idsa/autogen/artifact/a4212311-86e4-40b3-ace3-ef29cd687cf9")
        @RequestParam(value = "requestedArtifact") URI artifactId,
        @Parameter(description = "The contract offer for the requested resource.")
        @RequestBody(required = false) String contractOffer) {
        if (tokenProvider.getTokenJWS() == null) {
            return respondRejectUnauthorized(recipient, null);
        }

        Map<ResponseType, String> map;
        try {
            // Start policy negotiation.
            Response response = negotiationService
                .sendContractRequest(contractOffer, artifactId, recipient);
            try {
                // Read response.
                map = contractResponseService.handleResponse(response);
            } catch (MessageResponseException e) {
                // The response is null
                LOGGER.warn("Received no response message.");
                return new ResponseEntity<>("Received no response.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IllegalArgumentException| MessageException exception) {
            LOGGER.warn("Contract negotiation failed. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Contract negotiation has failed. "
                + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Get contract id.
        URI agreementId = negotiationService.contractAccepted(map, contractResponseService.getHeader());
        if (agreementId == null) {
            return returnRejectionMessage(map);
        }

        return new ResponseEntity<>(String.format("Negotiation has been successful. Contract "
            + "agreement: %s", agreementId), HttpStatus.OK);
    }

    /**
     * Requests data from an external connector by building an ArtifactRequestMessage.
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
        @Parameter(description = "The URI of the contract agreement.",
            example = "https://w3id.org/idsa/autogen/contractAgreement/a4212311-86e4-40b3-ace3-ef29cd687cf9")
        @RequestParam(value = "transferContract", required = false) URI contractId,
        @Parameter(description = "A unique validation key.", required = true)
        @RequestParam("key") UUID key) {
        if (tokenProvider.getTokenJWS() == null) {
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

        Response response;
        try {
            // Send ArtifactRequestMessage.
            artifactRequestService.setParameter(recipient, artifactId, contractId);
            response = artifactRequestService.sendMessage("");
        } catch (MessageException exception) {
            // Failed to send a description request message
            LOGGER.info("Could not connect to request message service.");
            return new ResponseEntity<>("Failed to send an ids message.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<ResponseType, String> map;
        try {
            map = artifactResponseService.handleResponse(response);
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
            artifactResponseService.saveData(payload, key);
            return new ResponseEntity<>(String.format("Saved at: %s\nResponse: " +
                "%s", key, payload), HttpStatus.OK);
        } catch (Exception exception) {
            LOGGER.warn("Could not save data to database. [exception=({})]",
                exception.getMessage());
            return new ResponseEntity<>("Failed to save to database.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * The request was unauthorized.
     *
     * @param recipient The recipient url.
     * @param requestedArtifact The id of the requested artifact.
     * @return An http response.
     */
    private ResponseEntity<String> respondRejectUnauthorized(URI recipient, URI requestedArtifact) {
        LOGGER.debug(
            "Unauthorized call. No DAT token found. [recipient=({}), requestedArtifact=({})]",
            recipient.toString(), requestedArtifact.toString());

        return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Checks for rejection or contract rejection message.
     */
    private ResponseEntity<String> returnRejectionMessage(Map<ResponseType, String> map) {
        if (map.get(ResponseType.REJECTION) != null) {
            return new ResponseEntity<>(ResponseType.REJECTION + ": "
                + map.get(ResponseType.REJECTION), HttpStatus.OK);
        } else if (map.get(ResponseType.CONTRACT_REJECTION) != null) {
            return new ResponseEntity<>(ResponseType.CONTRACT_REJECTION + ": "
                + map.get(ResponseType.CONTRACT_REJECTION), HttpStatus.OK);
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
            return resourceService.getResource(resourceId) != null;
        } catch (ResourceException exception) {
            return false;
        }
    }
}
