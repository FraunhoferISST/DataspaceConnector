/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.controller.messages;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.PersistenceException;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.exceptions.ContractException;
import io.dataspaceconnector.exceptions.InvalidInputException;
import io.dataspaceconnector.exceptions.MessageException;
import io.dataspaceconnector.exceptions.MessageResponseException;
import io.dataspaceconnector.exceptions.ResourceNotFoundException;
import io.dataspaceconnector.services.EntityPersistenceService;
import io.dataspaceconnector.services.EntityUpdateService;
import io.dataspaceconnector.services.messages.types.ArtifactRequestService;
import io.dataspaceconnector.services.messages.types.ContractAgreementService;
import io.dataspaceconnector.services.messages.types.ContractRequestService;
import io.dataspaceconnector.services.messages.types.DescriptionRequestService;
import io.dataspaceconnector.services.resources.AgreementService;
import io.dataspaceconnector.services.usagecontrol.ContractManager;
import io.dataspaceconnector.utils.ControllerUtils;
import io.dataspaceconnector.utils.MessageUtils;
import io.dataspaceconnector.utils.RuleUtils;
import io.dataspaceconnector.view.AgreementViewAssembler;
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

/**
 * This controller provides the endpoint for sending a contract request message and starting the
 * metadata and data exchange.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class ContractRequestMessageController {

    /**
     * Service for contract request message handling.
     */
    private final @NonNull ContractRequestService contractReqSvc;

    /**
     * Service for artifact request message handling.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

    /**
     * Service for description request message handling.
     */
    private final @NonNull DescriptionRequestService descReqSvc;

    /**
     * Service for contract agreement message handling.
     */
    private final @NonNull ContractAgreementService agreementSvc;

    /**
     * Service for updating database entities.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Assemblers DTOs for agreements.
     */
    private final @NonNull AgreementViewAssembler agreementAsm;

    /**
     * Used for gaining access to agreements.
     */
    private final @NonNull AgreementService agreementService;

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

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
            RuleUtils.validateRuleTarget(ruleList);
            final var request = contractManager.buildContractRequest(ruleList);

            // CONTRACT NEGOTIATION ----------------------------------------------------------------
            // Send and validate contract request/response message.
            response = contractReqSvc.sendMessage(recipient, request);
            if (!contractReqSvc.validateResponse(response)) {
                // If the response is not a contract agreement message, show the response.
                final var content = contractReqSvc.getResponseContent(response);
                return ControllerUtils.respondWithMessageContent(content);
            }

            // Read and process the response message.
            final var payload = MessageUtils.extractPayloadFromMultipartMessage(response);
            final var agreement = contractManager.validateContractAgreement(payload, request);

            // Send and validate contract agreement/response message.
            response = agreementSvc.sendMessage(recipient, agreement);
            if (!agreementSvc.validateResponse(response)) {
                // If the response is not a notification message, show the response.
                final var content = agreementSvc.getResponseContent(response);
                return ControllerUtils.respondWithMessageContent(content);
            }

            // Save contract agreement to database.
            agreementId = persistenceSvc.saveContractAgreement(agreement);
            if (log.isDebugEnabled()) {
                log.debug("Policy negotiation success. Saved agreement. [agreemendId=({})].",
                        agreementId);
            }

            // DESCRIPTION REQUESTS ----------------------------------------------------------------
            // Iterate over list of resource ids to send description request messages for each.
            for (final var resource : resources) {
                // Send and validate description request/response message.
                response = descReqSvc.sendMessage(recipient, resource);
                if (!descReqSvc.validateResponse(response)) {
                    // If the response is not a description response message, show the response.
                    final var content = descReqSvc.getResponseContent(response);
                    return ControllerUtils.respondWithMessageContent(content);
                }

                // Read and process the response message. Save resource, recipient, and agreement
                // id to database.
                persistenceSvc.saveMetadata(response, artifacts, download, recipient);
            }

            updateService.linkArtifactToAgreement(artifacts, agreementId);

            // ARTIFACT REQUESTS -------------------------------------------------------------------
            // Download data depending on user input.
            if (download) {
                // Iterate over list of resource ids to send artifact request messages for each.
                for (final var artifact : artifacts) {
                    // Send and validate artifact request/response message.
                    final var transferContract = agreement.getId();
                    response = artifactReqSvc.sendMessage(recipient, artifact, transferContract);
                    if (!artifactReqSvc.validateResponse(response)) {
                        // If the response is not an artifact response message, show the response.
                        // Ignore when data could not be downloaded, because the artifact request
                        // can be triggered later again.
                        final var content = artifactReqSvc.getResponseContent(response);
                        if (log.isDebugEnabled()) {
                            log.debug("Data could not be loaded. [content=({})]", content);
                        }
                    }

                    // Read and process the response message.
                    try {
                        persistenceSvc.saveData(response, artifact);
                    } catch (ResourceNotFoundException | MessageResponseException exception) {
                        // Ignore that the data saving failed. Another try can take place later.
                        if (log.isWarnEnabled()) {
                            log.warn("Could not save data for artifact."
                                            + "[artifact=({}), exception=({})]",
                                    artifact, exception.getMessage());
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

        final var entity = agreementAsm.toModel(agreementService.get(agreementId));

        final var headers = new HttpHeaders();
        headers.setLocation(entity.getLink("self").get().toUri());

        return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
    }
}
