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
package io.dataspaceconnector.controller.message.ids;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.exception.InvalidInputException;
import io.dataspaceconnector.common.exception.MessageException;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.exception.RdfBuilderException;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.common.ids.policy.RuleUtils;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.controller.message.tag.MessageDescription;
import io.dataspaceconnector.controller.message.tag.MessageName;
import io.dataspaceconnector.controller.resource.view.agreement.AgreementViewAssembler;
import io.dataspaceconnector.controller.util.ResponseUtils;
import io.dataspaceconnector.service.ArtifactDataDownloader;
import io.dataspaceconnector.service.ContractNegotiator;
import io.dataspaceconnector.service.EntityUpdateService;
import io.dataspaceconnector.service.MetadataDownloader;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.resource.type.AgreementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
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
import java.util.Objects;
import java.util.UUID;

/**
 * This controller provides the endpoint for sending a contract request message and starting the
 * metadata and data exchange.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = MessageName.MESSAGES, description = MessageDescription.MESSAGES)
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
     * Template for triggering Camel routes.
     */
    private final @NonNull ProducerTemplate template;

    /**
     * The CamelContext required for constructing the {@link ProducerTemplate}.
     */
    private final @NonNull CamelContext context;

    /**
     * Service for handle application.properties settings.
     */
    private final @NonNull ConnectorConfig connectorConfig;

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
    @Operation(summary = "Send an IDS ContractRequestMessage to start the contract negotiation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
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
            @RequestBody final List<Rule> ruleList) {
        if (connectorConfig.isIdscpEnabled()) {
            UUID agreementId;
            final var result = template.send("direct:contractRequestSender",
                    ExchangeBuilder.anExchange(context)
                            .withProperty(ParameterUtils.RECIPIENT_PARAM, recipient)
                            .withProperty(ParameterUtils.RESOURCES_PARAM, resources)
                            .withProperty(ParameterUtils.ARTIFACTS_PARAM, artifacts)
                            .withProperty(ParameterUtils.DOWNLOAD_PARAM, download)
                            .withProperty(ParameterUtils.RULE_LIST_PARAM, ruleList)
                            .build());

            final var response = result.getIn().getBody(Response.class);
            if (response != null) {
                agreementId = result.getProperty(ParameterUtils.AGREEMENT_ID_PARAM, UUID.class);
            } else {
                final var responseEntity =
                    toObjectResponse(result.getIn().getBody(ResponseEntity.class));
                return Objects.requireNonNullElseGet(responseEntity,
                        () -> new ResponseEntity<Object>("An internal server error occurred.",
                                HttpStatus.INTERNAL_SERVER_ERROR));
            }

            // Return response entity containing the locations of the contract agreement, the
            // downloaded resources, and the downloaded data.

            final var entity = agreementAsm.toModel(agreementService.get(agreementId));

            final var headers = new HttpHeaders();
            headers.setLocation(entity.getRequiredLink("self").toUri());

            return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
        } else {
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
                return ResponseUtils.respondInvalidInput(exception);
            } catch (ConstraintViolationException | RdfBuilderException exception) {
                // If contract request could not be built.
                return ResponseUtils.respondFailedToBuildContractRequest(exception);
            } catch (PersistenceException exception) {
                // If metadata, data, or contract agreement could not be stored.
                return ResponseUtils.respondFailedToStoreEntity(exception);
            } catch (MessageException exception) {
                return ResponseUtils.respondIdsMessageFailed(exception);
            } catch (MessageResponseException | IllegalArgumentException e) {
                // If the response message is invalid or malformed.
                return ResponseUtils.respondReceivedInvalidResponse(e);
            } catch (ContractException e) {
                // If the contract agreement is invalid.
                return ResponseUtils.respondNegotiationAborted();
            } catch (UnexpectedResponseException e) {
                // If the response is not as expected.
                return ResponseUtils.respondWithContent(e.getContent());
            }
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

    @SuppressWarnings("unchecked")
    private static ResponseEntity<Object> toObjectResponse(final ResponseEntity<?> response) {
        return (ResponseEntity<Object>) response;
    }
}
