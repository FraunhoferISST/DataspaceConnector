package de.fraunhofer.isst.dataspaceconnector.controller.messages;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnexpectedResponseType;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyManagementService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class ContractRequestMessageController {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyManagementService.class);

    /**
     * Service for policy management.
     */
    private final @NonNull PolicyManagementService managementService;

    /**
     * Service for message handling.
     */
    private final @NonNull MessageService messageService;

    /**
     * Starts a contract, metadata, and data exchange with an external connector.
     *
     * @param recipient The recipient.
     * @param resourceList List of requested resources by IDs.
     * @param artifactList List of requested artifacts by IDs.
     * @param download Download data directly after successful contract and description request.
     * @param ruleList List of rules that should be used within a contract request.
     * @return The response entity.
     */
    @PostMapping("/contract")
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
            @RequestParam(value = "resourceIds") final List<URI> resourceList,
            @Parameter(description = "List of ids artifacts that should be requested.")
            @RequestParam(value = "artifactIds") final List<URI> artifactList,
//            @Parameter(description = "Indicates whether the connector should listen on remote "
//                    + "updates.") @RequestParam(value = "subscribe") final boolean subscribe,
            @Parameter(description = "Indicates whether the connector should automatically "
                    + "download data of an artifact.")
            @RequestParam(value = "download") final boolean download,
            @Parameter(description = "List of ids rules with an artifact id as target.")
            @RequestBody final List<Rule> ruleList) {
        final var agreementLocations = new ArrayList<URI>();
        final var resourceLocations = new ArrayList<URI>();
        final var dataLocations = new ArrayList<URI>();

        Map<String, String> response = null;
        try {
            // Validate input for contract request.
            final var contractRequest = managementService
                    .validateAndBuildContractRequest(ruleList);

            // Send and validate contract request/response message.
            // NOTE: Not in a separate method as we need the response in the catch-block.
            response = messageService.sendContractRequestMessage(recipient, contractRequest);
            messageService.validateContractRequestResponseMessage(response);

            // Read and process the response message.
            final var contractAgreement = managementService
                    .readAndValidateAgreementFromResponse(response, contractRequest);

            // Send and validate contract request/response message.
            // NOTE: Not in a separate method as we need the response in the catch-block.
            response = messageService.sendContractAgreementMessage(recipient, contractAgreement);
            messageService.validateContractAgreementResponseMessage(response);

            // Save contract agreement to database.
            final var agreementId = managementService.saveContractAgreement(contractAgreement);
            agreementLocations.add(agreementId);
            LOGGER.info("Policy negotiation success. Saved agreement: " + agreementId);

            // Iterate over list of resource ids to send description request messages for each.
            for (final var resource : resourceList) {
                // Send and validate description request/response message.
                // NOTE: Not in a separate method as we need the response in the catch-block.
                response = messageService.sendDescriptionRequestMessage(recipient, resource);
                messageService.validateDescriptionResponseMessage(response);

                // Read and process the response message. Save resource to database.
                final var resourceId = messageService.saveResource(response, artifactList);
                resourceLocations.add(resourceId);
            }

            // Download data depending on user input.
            if (download) {
                // Iterate over list of resource ids to send artifact request messages for each.
                for (final var artifact : artifactList) {
                    // Send and validate artifact request/response message.
                    // NOTE: Not in a separate method as we need the response in the catch-block.
                    final var remoteId = contractAgreement.getId();
                    response = messageService.sendArtifactRequestMessage(recipient, artifact,
                            remoteId);
                    messageService.validateArtifactResponseMessage(response);

                    // Read and process the response message.
                    final var artifactId = messageService.saveData(response, artifact);
                    resourceLocations.add(artifactId);
                }
            }
        } catch (InvalidContractException exception) {
            return ControllerUtils.respondInvalidInput(exception);
        } catch (ConstraintViolationException exception) {
            return ControllerUtils.respondFailedToBuildContractRequest(exception);
        } catch (PersistenceException exception) {
            return ControllerUtils.respondFailedToStoreEntity(exception);
        } catch (MessageException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        } catch (UnexpectedResponseType exception) {
            // If the response is not a contract agreement message, show the response.
            return messageService.returnResponseMessageContent(response);
        } catch (MessageResponseException | IllegalArgumentException | ContractException exception) {
            return ControllerUtils.respondReceivedInvalidResponse(exception);
        }

        return new ResponseEntity<>(new HashMap<String, List<URI>>() {{
            put("agreement", agreementLocations);
            put("resources", resourceLocations);
            put("data", dataLocations);
        }}, HttpStatus.CREATED);
    }
}
