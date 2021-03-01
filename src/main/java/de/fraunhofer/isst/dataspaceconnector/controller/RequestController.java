package de.fraunhofer.isst.dataspaceconnector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService.ResponseType;
import de.fraunhofer.isst.dataspaceconnector.services.messages.NegotiationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.ArtifactMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.ContractMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.DescriptionMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.services.utils.ValidationUtils;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private final DapsTokenProvider tokenProvider;
    private final ArtifactMessageService artifactMessageService;
    private final DescriptionMessageService descriptionMessageService;
    private final ContractMessageService contractMessageService;
    private final NegotiationService negotiationService;
    private final ResourceService resourceService;
    private final ObjectMapper objectMapper;


    /**
     * Constructor for RequestController
     *
     * @param tokenProvider The token provider
     * @param artifactMessageService The service for artifact messages
     * @param descriptionMessageService The service for description messages
     * @param contractMessageService The service for contract messages
     * @param negotiationService The service for negotiations
     * @param requestedResourceService The service for the requested resources
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public RequestController(DapsTokenProvider tokenProvider,
                             ArtifactMessageService artifactMessageService,
                             DescriptionMessageService descriptionMessageService,
                             ContractMessageService contractMessageService,
                             NegotiationService negotiationService,
                             RequestedResourceServiceImpl requestedResourceService)
        throws IllegalArgumentException {
        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (artifactMessageService == null)
            throw new IllegalArgumentException("The ArtifactMessageService cannot be null.");

        if (descriptionMessageService == null)
            throw new IllegalArgumentException("The DescriptionMessageService cannot be null.");

        if (contractMessageService == null)
            throw new IllegalArgumentException("The ContractMessageService cannot be null.");

        if (negotiationService == null)
            throw new IllegalArgumentException("The NegotiationService cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The RequestedResourceServiceImpl cannot be null.");

        this.tokenProvider = tokenProvider;
        this.artifactMessageService = artifactMessageService;
        this.descriptionMessageService = descriptionMessageService;
        this.contractMessageService = contractMessageService;
        this.negotiationService = negotiationService;
        this.resourceService = requestedResourceService;
        this.objectMapper = new ObjectMapper();
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/description", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> requestMetadata(
        @Parameter(description = "The URI of the requested IDS connector.", required = true,
            example = "https://localhost:8080/api/ids/data")
        @RequestParam("recipient") URI recipient,
        @Parameter(description = "The URI of the requested resource.",
            example = "https://w3id.org/idsa/autogen/resource/a4212311-86e4-40b3-ace3-ef29cd687cf9")
        @RequestParam(value = "requestedResource", required = false) URI resourceId) {
        if (tokenProvider.getDAT() == null) {
            return respondRejectUnauthorized(recipient, resourceId);
        }

        Map<String, String> response;
        try {
            // Send DescriptionRequestMessage.
            descriptionMessageService.setRequestParameters(recipient, resourceId);
            response = descriptionMessageService.sendRequestMessage("");
        } catch (MessageBuilderException exception) {
            // Failed to build the description request message.
            LOGGER.warn("Failed to build a request. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to build the ids message.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessageResponseException exception) {
            // Failed to read the description response message.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to read the ids response message.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessageNotSentException exception) {
            // Failed to send the description request message.
            LOGGER.warn("Failed to send a request. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to send the ids message.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String header, payload;
        try {
            header = response.get("header");
            payload = response.get("payload");
        } catch (Exception exception) {
            // Failed to read the message parts.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to read the ids response message.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Get response message type.
        final var messageType = descriptionMessageService.getResponseType(header);
        if (messageType != ResponseType.DESCRIPTION_RESPONSE)
            return returnRejectionMessage(messageType, response);

        if (resourceId != null) {
            // Save metadata to database.
            try {
                final var validationKey = descriptionMessageService
                        .saveMetadata(payload, resourceId, recipient);
                return new ResponseEntity<>("Validation: " + validationKey +
                    "\nResponse: " + payload, HttpStatus.OK);
            } catch (InvalidResourceException exception) {
                LOGGER.warn("Could not save metadata to database. [exception=({})]",
                    exception.getMessage());
                return new ResponseEntity<>(exception.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // Return self-description.
            return new ResponseEntity<>(payload, HttpStatus.OK);
        }
    }

    /**
     * Sends a contract request to a connector by building an ContractRequestMessage.
     *
     * @param recipient The URI of the requested IDS connector.
     * @param artifactId The URI of the requested artifact.
     * @param contractOffer The contract offer for the requested resource.
     * @return OK or error response.
     */
    @Operation(summary = "Contract Request",
        description = "Send a contract request to another IDS connector.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
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
        if (tokenProvider.getDAT() == null) {
            return respondRejectUnauthorized(recipient, null);
        }

        Map<String, String> response;
        try {
            // Start policy negotiation.
            final var request = negotiationService.buildContractRequest(contractOffer, artifactId);
            // Send ContractRequestMessage.
            contractMessageService.setRequestParameters(recipient, request.getId());
            response = contractMessageService.sendRequestMessage(request.toRdf());
        } catch (IllegalArgumentException exception) {
            LOGGER.warn("Failed to build contract request. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to build contract request.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessageBuilderException exception) {
            // Failed to build the contract request message.
            LOGGER.warn("Failed to build a request. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to build the ids message.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessageResponseException exception) {
            // Failed to read the contract response message.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to read the ids response message.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessageNotSentException exception) {
            // Failed to send the contract request message.
            LOGGER.warn("Failed to send a request. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to send the ids message.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String header, payload;
        try {
            header = response.get("header");
            payload = response.get("payload");
        } catch (Exception exception) {
            // Failed to read the message parts.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to read the ids response message.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Get response message type.
        final var messageType = contractMessageService.getResponseType(header);
        // TODO Add further responses (on contract offer or contract response).
        if (messageType != ResponseType.CONTRACT_AGREEMENT)
            return returnRejectionMessage(messageType, response);

        // Get contract id.
        URI agreementId;
        try {
            agreementId = negotiationService.contractAccepted(recipient, header, payload);
        } catch (ContractException exception) {
            // Failed to read the contract.
            LOGGER.debug("Could not read contract. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to read the received contract.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessageException exception) {
            // Failed to send contract agreement confirmation.
            LOGGER.warn("Failed to send contract agreement. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Negotiation sequence was not fully completed.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (agreementId == null) {
            // Failed to read the contract agreement.
            LOGGER.debug("Received invalid contract agreement.");
            return new ResponseEntity<>("Received invalid contract agreement.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(String.valueOf(agreementId), HttpStatus.OK);
    }

    /**
     * Requests data from an external connector by building an ArtifactRequestMessage.
     *
     * @param recipient         The target connector uri.
     * @param artifactId        The requested artifact uri.
     * @param contractId        The URI of the contract agreement.
     * @param key               a {@link java.util.UUID} object.
     * @return OK or error response.
     */
    @Operation(summary = "Artifact Request",
        description = "Request data from another IDS connector. " +
            "INFO: Before an artifact can be requested, the metadata must be queried. The key" +
            " generated in this process must be passed in the artifact query.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
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
            @RequestParam("key") UUID key,
            @Parameter(description = "The query parameters and headers to use when fetching the " +
                    "data from the backend system.")
            @RequestBody(required = false) QueryInput queryInput) {
        if (tokenProvider.getDAT() == null) {
            return respondRejectUnauthorized(recipient, artifactId);
        }

        if (!resourceExists(key)) {
            // The resource does not exist.
            LOGGER.warn(String.format("Failed data request due to invalid key.\nRecipient: " +
                    "%s\nrequestedArtifact:%s\nkey:%s", recipient.toString(),
                artifactId.toString(), key.toString()));
            return new ResponseEntity<>("Your key is not valid. Please request metadata first.",
                HttpStatus.FORBIDDEN);
        }

        try {
            ValidationUtils.validateQueryInput(queryInput);
        } catch (IllegalArgumentException exception) {
            // There is an empty key or value string in the params or headers map
            LOGGER.debug("Invalid input for headers or params. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Invalid input for headers or params. ",
                    HttpStatus.BAD_REQUEST);
        }

        Map<String, String> response;
        try {
            // Send ArtifactRequestMessage.
            artifactMessageService.setRequestParameters(recipient, artifactId, contractId);
            response = artifactMessageService.sendRequestMessage(objectMapper.writeValueAsString(queryInput));
        } catch (MessageBuilderException exception) {
            // Failed to build the artifact request message.
            LOGGER.warn("Failed to build a request. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to build the ids message.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessageResponseException exception) {
            // Failed to read the artifact response message.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Received invalid ids response message.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessageNotSentException exception) {
            // Failed to send the artifact request message.
            LOGGER.warn("Failed to send a request. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to send the ids message.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException exception) {
            // Could not parse query input (params and headers).
            LOGGER.debug("Could not parse query input from request body. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Could not parse query input from request body.",
                    HttpStatus.BAD_REQUEST);
        }

        String header, payload;
        try {
            header = response.get("header");
            payload = response.get("payload");
        } catch (Exception exception) {
            // Failed to read the message parts.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Failed to read the ids response message.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Get response message type.
        final var messageType = artifactMessageService.getResponseType(header);
        if (messageType != ResponseType.ARTIFACT_RESPONSE)
            return returnRejectionMessage(messageType, response);

        try {
            // Save contract agreement id and requested artifact.
            final var resource = (RequestedResource) resourceService.getResource(key);
            resource.setContractAgreement(contractId);
            resource.setRequestedArtifact(artifactId);
        } catch (ResourceException exception) {
            LOGGER.warn("Could not update resource. [exception=({})]",
                    exception.getMessage());
            return new ResponseEntity<>("Could not update metadata.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            // Save data to database.
            artifactMessageService.saveData(payload, key);
            return new ResponseEntity<>(String.format("Saved at: %s\nResponse: " +
                "%s", key, payload), HttpStatus.OK);
        } catch (ResourceException exception) {
            LOGGER.warn("Could not save data to database. [exception=({})]",
                exception.getMessage());
            return new ResponseEntity<>("Failed to save data to database.",
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
     *
     * @param responseType The type of the response
     * @param response The response content
     * @return The response message
     */
    private ResponseEntity<String> returnRejectionMessage(ResponseType responseType,
        Map<String, String> response) {
        if (responseType == ResponseType.REJECTION) {
            return new ResponseEntity<>(ResponseType.REJECTION + ": "
                + response.get("payload"), HttpStatus.OK);
        } else if (responseType == ResponseType.CONTRACT_REJECTION) {
            return new ResponseEntity<>(ResponseType.CONTRACT_REJECTION + ": "
                + response.get("payload"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unexpected response: \n" + response, HttpStatus.OK);
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
