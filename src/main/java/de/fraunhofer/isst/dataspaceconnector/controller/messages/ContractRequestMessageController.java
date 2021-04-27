package de.fraunhofer.isst.dataspaceconnector.controller.messages;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidInputException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.services.EntityUpdateService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyManagementService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import de.fraunhofer.isst.dataspaceconnector.view.AgreementViewAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class ContractRequestMessageController {

    /**
     * Service for policy management.
     */
    private final @NonNull PolicyManagementService managementService;

    /**
     * Service for message handling.
     */
    private final @NonNull MessageService messageService;

    /**
     * Service for updating database entities.
     */
    private final @NonNull EntityUpdateService updateService;


    private final @NonNull AgreementViewAssembler agreementAssembler;
    private final @NonNull AgreementService agreementService;

    /**
     * Starts a contract, metadata, and data exchange with an external connector.
     *
     * @param recipient    The recipient.
     * @param resources List of requested resources by IDs.
     * @param artifacts List of requested artifacts by IDs.
     * @param download     Download data directly after successful contract and description request.
     * @param ruleList     List of rules that should be used within a contract request.
     * @return The response entity.
     */
    @PostMapping(value = "/contract")
    @Operation(summary = "Send ids description request message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    public ResponseEntity<Object> sendContractRequestMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "List of ids resource that should be requested.")
            @RequestParam(value = "resourceIds") final List<URI> resources,
            @Parameter(description = "List of ids artifacts that should be requested.")
            @RequestParam(value = "artifactIds") final List<URI> artifacts,
//            @Parameter(description = "Indicates whether the connector should listen on remote "
//                    + "updates.") @RequestParam(value = "subscribe") final boolean subscribe,
            @Parameter(description = "Indicates whether the connector should automatically "
                    + "download data of an artifact.")
            @RequestParam(value = "download") final boolean download,
            @Parameter(description = "List of ids rules with an artifact id as target.")
            @RequestBody final List<Rule> ruleList) {
        UUID agreementId;

        Map<String, String> response;
        try {
            // Validate input for contract request.
            PolicyUtils.validateRuleTarget(ruleList);
            final var request = managementService.buildContractRequest(ruleList);

            // CONTRACT NEGOTIATION ----------------------------------------------------------------
            // Send and validate contract request/response message.
            response = messageService.sendContractRequestMessage(recipient, request);
            if (!messageService.validateContractRequestResponseMessage(response)) {
                // If the response is not a contract agreement message, show the response.
                final var content = messageService.getContent(response);
                return ControllerUtils.respondWithMessageContent(content);
            }

            // Read and process the response message.
            final var agreement = managementService
                    .readAndValidateAgreementFromResponse(response, request);

            // Send and validate contract agreement/response message.
            response = messageService.sendContractAgreementMessage(recipient, agreement);
            if (!messageService.validateContractAgreementResponseMessage(response)) {
                // If the response is not a notification message, show the response.
                final var content = messageService.getContent(response);
                return ControllerUtils.respondWithMessageContent(content);
            }

            // Save contract agreement to database.
            agreementId = managementService.saveContractAgreement(agreement, true);
            if (log.isDebugEnabled()) {
                log.debug("Policy negotiation success. Saved agreement: " + agreementId);
            }

            // DESCRIPTION REQUESTS ----------------------------------------------------------------
            // Iterate over list of resource ids to send description request messages for each.
            for (final var resource : resources) {
                // Send and validate description request/response message.
                response = messageService.sendDescriptionRequestMessage(recipient, resource);
                if (!messageService.validateDescriptionResponseMessage(response)) {
                    // If the response is not a description response message, show the response.
                    final var content = messageService.getContent(response);
                    return ControllerUtils.respondWithMessageContent(content);
                }

                // Read and process the response message. Save resource, recipient, and agreement
                // id to database.
                // TODO Check if a resource with remoteId is already stored on consumer side, if yes, do NOT create a new resource, but update it and all children
                // TODO store remote address (= recipient) to artifact (RemoteConsumerData??)
                messageService.saveMetadata(response, artifacts, download, recipient);
            }

            updateService.linkArtifactToAgreement(artifacts, agreementId);

            // ARTIFACT REQUESTS -------------------------------------------------------------------
            // Download data depending on user input.
            if (download) {
                // Iterate over list of resource ids to send artifact request messages for each.
                for (final var artifact : artifacts) {


                    // Send and validate artifact request/response message.
                    final var transferContract = agreement.getId();
                    response = messageService.sendArtifactRequestMessage(recipient, artifact,
                            transferContract);
                    if (!messageService.validateArtifactResponseMessage(response)) {
                        // If the response is not an artifact response message, show the response.
                        // Ignore when data could not be downloaded, because the artifact request
                        // can be triggered later again.
                        final var content = messageService.getContent(response);
                        if (log.isDebugEnabled()) {
                            log.debug("Data could not be loaded: \n" + content);
                        }
                    }

                    // Read and process the response message.
                    try {
                        messageService.saveData(response, artifact);
                    } catch (ResourceNotFoundException | MessageResponseException exception) {
                        // Ignore that the data saving failed. Another try can take place later.
                        if (log.isWarnEnabled()) {
                            log.warn("Could not save data for artifact with id" + artifact
                                    + ". [exception=({})]", exception.getMessage());
                        }
                    }
                }
            }
        } catch (InvalidInputException exception) {
            return ControllerUtils.respondInvalidInput(exception);
        } catch (ConstraintViolationException exception) {
            return ControllerUtils.respondFailedToBuildContractRequest(exception);
        } catch (PersistenceException exception) {
            return ControllerUtils.respondFailedToStoreEntity(exception);
        } catch (MessageException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        } catch (MessageResponseException | IllegalArgumentException | ContractException e) {
            return ControllerUtils.respondReceivedInvalidResponse(e);
        }

        // Return response entity containing the locations of the contract agreement, the
        // downloaded resources, and the downloaded data.

        final var entity = agreementAssembler.toModel(agreementService.get(agreementId));

        final var headers = new HttpHeaders();
        headers.setLocation(entity.getLink("self").get().toUri());

        return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
    }
}
