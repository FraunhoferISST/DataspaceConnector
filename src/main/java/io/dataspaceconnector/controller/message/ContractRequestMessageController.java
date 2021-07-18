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
package io.dataspaceconnector.controller.message;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.controller.resource.view.AgreementViewAssembler;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.exception.InvalidInputException;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.exception.RdfBuilderException;
import io.dataspaceconnector.service.ArtifactDataDownloader;
import io.dataspaceconnector.service.ContractNegotiator;
import io.dataspaceconnector.service.EntityUpdateService;
import io.dataspaceconnector.service.MetadataDownloader;
import io.dataspaceconnector.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.resource.AgreementService;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import io.dataspaceconnector.controller.util.ControllerUtils;
import io.dataspaceconnector.util.MessageUtils;
import io.dataspaceconnector.util.RuleUtils;
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

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * This controller provides the endpoint for sending a contract request message and starting the
 * metadata and data exchange.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "Messages", description = "Endpoints for invoke sending messages")
public class ContractRequestMessageController {
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
     * Negotiates the contract.
     */
    private final @NonNull ContractNegotiator negotiator;

    /**
     * Downloads metadata.
     */
    private final @NonNull MetadataDownloader metadataDownloader;

    /**
     * Downloads artifact's data.
     */
    private final @NonNull ArtifactDataDownloader artifactDataDownloader;

    /**
     * Starts a contract, metadata, and data exchange with an external connector.
     *
     * @param recipient The recipient.
     * @param resources List of requested resources by IDs.
     * @param artifacts List of requested artifacts by IDs.
     * @param download  download data directly after successful contract and description request.
     * @param ruleList  List of rules that should be used within a contract request.
     * @return The response entity.
     */
    @PostMapping("/contract")
    @Operation(summary = "Send IDS contract request message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "502", description = "Bad gateway")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    public ResponseEntity<Object> sendMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "List of ids resource that should be requested.")
            @RequestParam("resourceIds") final List<URI> resources,
            @Parameter(description = "List of ids artifacts that should be requested.")
            @RequestParam("artifactIds") final List<URI> artifacts,
            @Parameter(description = "Indicates whether the connector should automatically "
                    + "download data of an artifact.")
            @RequestParam("download") final boolean download,
            @Parameter(description = "List of ids rules with an artifact id as target.")
            @RequestBody final List<Rule> ruleList) throws UnexpectedResponseException {
        try {
            // Validates user input.
            RuleUtils.validateRuleTarget(ruleList);

            // Initiate contract negotiation.
            final var agreementId = negotiator.negotiate(recipient, ruleList);

            // Download metadata.
            downloadMetadata(recipient, resources, artifacts, download, agreementId);

            // Download data, if requested.
            if (download) {
                artifactDataDownloader.download(recipient, artifacts, agreementId);
            }

            return respondWithCreatedAgreement(agreementId);
        } catch (InvalidInputException exception) {
            // If the input rules are malformed.
            return ControllerUtils.respondInvalidInput(exception);
        } catch (ConstraintViolationException | RdfBuilderException exception) {
            // If contract request could not be built.
            return ControllerUtils.respondFailedToBuildContractRequest(exception);
        } catch (PersistenceException exception) {
            // If metadata, data, or contract agreement could not be stored.
            return ControllerUtils.respondFailedToStoreEntity(exception);
        } catch (MessageException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        } catch (MessageResponseException | IllegalArgumentException e) {
            // If the response message is invalid or malformed.
            return ControllerUtils.respondReceivedInvalidResponse(e);
        } catch (ContractException e) {
            // If the contract agreement is invalid.
            return ControllerUtils.respondNegotiationAborted();
        }
    }

    private void downloadMetadata(final URI recipient, final List<URI> resources,
                                  final List<URI> artifacts, final boolean download,
                                  final UUID agreementId) throws PersistenceException,
            MessageResponseException, MessageException, UnexpectedResponseException {
        metadataDownloader.download(recipient, resources, artifacts, download);
        updateService.linkArtifactToAgreement(artifacts, agreementId);
    }

    private ResponseEntity<Object> respondWithCreatedAgreement(final UUID agreementId) {
        final var entity = agreementAsm.toModel(agreementService.get(agreementId));

        final var headers = new HttpHeaders();
        headers.setLocation(entity.getRequiredLink("self").toUri());

        return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
    }
}
