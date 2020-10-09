package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestServiceUtils;
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

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * This class provides endpoints for the communication with an IDS connector instance.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api/request")
@Tag(name = "Connector: IDS Connector Communication", description = "Endpoints for invoking external connector requests")
public class RequestController {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

    private TokenProvider tokenProvider;
    private ConnectorRequestServiceImpl requestMessageService;
    private ConnectorRequestServiceUtils connectorRequestServiceUtils;

    @Autowired
    /**
     * <p>Constructor for RequestController.</p>
     *
     * @param tokenProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider} object.
     * @param requestMessageService a {@link de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestServiceImpl} object.
     * @param connectorRequestServiceUtils a {@link de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestServiceUtils} object.
     */
    public RequestController(TokenProvider tokenProvider, ConnectorRequestServiceImpl requestMessageService, ConnectorRequestServiceUtils connectorRequestServiceUtils) {
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
     * @throws java.io.IOException if any.
     */
    @Operation(summary = "Artifact Request", description = "Request data from another IDS connector. " +
            "INFO: Before an artifact can be requested, the metadata must be queried. The key generated in this " +
            "process must be passed in the artifact query.")
    @RequestMapping(value = "/artifact", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> requestData(
            @Parameter(description = "The URI of the requested IDS connector.", required = true,
                    example = "https://localhost:8080/api/ids/data") @RequestParam("recipient") URI recipient,
            @Parameter(description = "The URI of the requested artifact.", required = true,
                    example = "https://w3id.org/idsa/autogen/artifact/a4212311-86e4-40b3-ace3-ef29cd687cf9")
            @RequestParam(value = "requestedArtifact") URI requestedArtifact,
            @Parameter(description = "A unique validation key.", required = true) @RequestParam("key") UUID key) throws IOException {
        if (tokenProvider.getTokenJWS() != null) {
            if (connectorRequestServiceUtils.resourceExists(key)) {
                Response response = requestMessageService.sendArtifactRequestMessage(recipient, requestedArtifact);
                String responseAsString = response.body().string();

                try {
                    connectorRequestServiceUtils.saveData(responseAsString, key);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>("Saved at: " + key + "\n"
                        + String.format("Success: %s", (response != null)) + "\n"
                        + String.format("Body: %s", responseAsString), HttpStatus.OK);
            } else {
                LOGGER.error("Key is not valid.");
                return new ResponseEntity<>("Your key is not valid. Please request metadata first.", HttpStatus.FORBIDDEN);
            }
        } else {
            LOGGER.error("No DAT token found");
            return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Actively requests metadata from an external connector by building an ArtifactRequestMessage.
     *
     * @param recipient         The target connector uri.
     * @param requestedArtifact The requested resource uri.
     * @return OK or error response.
     * @throws java.io.IOException if any.
     */
    @Operation(summary = "Description Request", description = "Request metadata from another IDS connector.")
    @RequestMapping(value = "/description", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> requestMetadata(
            @Parameter(description = "The URI of the requested IDS connector.", required = true,
                    example = "https://localhost:8080/api/ids/data") @RequestParam("recipient") URI recipient,
            @Parameter(description = "The URI of the requested resource.", required = false,
                    example = "https://w3id.org/idsa/autogen/resource/a4212311-86e4-40b3-ace3-ef29cd687cf9")
            @RequestParam(value = "requestedArtifact", required = false) URI requestedArtifact) throws IOException {
        if (tokenProvider.getTokenJWS() != null) {
            Response response = requestMessageService.sendDescriptionRequestMessage(recipient, requestedArtifact);
            String responseAsString = response.body().string();

            String hint = "";
            if (requestedArtifact != null) {
                try {
                    hint = "Validation key: " + connectorRequestServiceUtils.saveMetadata(responseAsString) + "\n";
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            return new ResponseEntity<>(hint
                    + String.format("Success: %s", (response != null)) + "\n"
                    + String.format("Body: %s", responseAsString), HttpStatus.OK);
        } else {
            LOGGER.error("No DAT token found");
            return new ResponseEntity<>("Please check your DAT token.", HttpStatus.UNAUTHORIZED);
        }
    }
}
