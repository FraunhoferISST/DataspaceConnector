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
package io.dataspaceconnector.controller.resource;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import de.fraunhofer.ids.messaging.protocol.UnexpectedResponseException;
import io.dataspaceconnector.controller.resource.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.resource.swagger.response.ResponseCode;
import io.dataspaceconnector.controller.resource.swagger.response.ResponseDescription;
import io.dataspaceconnector.controller.resource.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.AgreementView;
import io.dataspaceconnector.controller.resource.view.ArtifactView;
import io.dataspaceconnector.controller.resource.view.CatalogView;
import io.dataspaceconnector.controller.resource.view.ContractRuleView;
import io.dataspaceconnector.controller.resource.view.ContractView;
import io.dataspaceconnector.controller.resource.view.OfferedResourceView;
import io.dataspaceconnector.controller.resource.view.RepresentationView;
import io.dataspaceconnector.controller.resource.view.RequestedResourceView;
import io.dataspaceconnector.controller.resource.view.SubscriptionView;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.agreement.AgreementDesc;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.contract.ContractDesc;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.representation.RepresentationDesc;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.rule.ContractRuleDesc;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.service.BlockingArtifactReceiver;
import io.dataspaceconnector.service.ids.ConnectorService;
import io.dataspaceconnector.service.resource.AgreementService;
import io.dataspaceconnector.service.resource.ArtifactService;
import io.dataspaceconnector.service.resource.CatalogService;
import io.dataspaceconnector.service.resource.ContractService;
import io.dataspaceconnector.service.resource.RepresentationService;
import io.dataspaceconnector.service.resource.ResourceService;
import io.dataspaceconnector.service.resource.RetrievalInformation;
import io.dataspaceconnector.service.resource.RuleService;
import io.dataspaceconnector.service.resource.SubscriptionService;
import io.dataspaceconnector.service.usagecontrol.DataAccessVerifier;
import io.dataspaceconnector.util.QueryInput;
import io.dataspaceconnector.util.Utils;
import io.dataspaceconnector.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * This class contains all implementations of the {@link BaseResourceController}.
 */
public final class ResourceControllers {

    /**
     * Offers the endpoints for managing catalogs.
     */
    @RestController
    @RequestMapping("/api/catalogs")
    @Tag(name = ResourceName.CATALOGS, description = ResourceDescription.CATALOGS)
    public static class CatalogController
            extends BaseResourceController<Catalog, CatalogDesc, CatalogView, CatalogService> {
    }

    /**
     * Offers the endpoints for managing rules.
     */
    @RestController
    @RequestMapping("/api/rules")
    @Tag(name = ResourceName.RULES, description = ResourceDescription.RULES)
    public static class RuleController extends BaseResourceController<ContractRule,
            ContractRuleDesc, ContractRuleView, RuleService> {
    }

    /**
     * Offers the endpoints for managing representations.
     */
    @RestController
    @RequestMapping("/api/representations")
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
    public static class RepresentationController
            extends BaseResourceNotificationController<Representation, RepresentationDesc,
            RepresentationView, RepresentationService> {
    }

    /**
     * Offers the endpoints for managing contracts.
     */
    @RestController
    @RequestMapping("/api/contracts")
    @Tag(name = ResourceName.CONTRACTS, description = ResourceDescription.CONTRACTS)
    public static class ContractController
            extends BaseResourceController<Contract, ContractDesc, ContractView, ContractService> {
    }

    /**
     * Offers the endpoints for managing offered resources.
     */
    @RestController
    @RequestMapping("/api/offers")
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourceController
            extends BaseResourceNotificationController<OfferedResource, OfferedResourceDesc,
            OfferedResourceView, ResourceService<OfferedResource, OfferedResourceDesc>> {
    }

    /**
     * Offers the endpoints for managing requested resources.
     */
    @RestController
    @RequestMapping("/api/requests")
    @RequiredArgsConstructor
    @Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
    public static class RequestedResourceController
            extends BaseResourceController<RequestedResource, RequestedResourceDesc,
            RequestedResourceView, ResourceService<RequestedResource, RequestedResourceDesc>> {

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = ResponseDescription.METHOD_NOT_ALLOWED)})
        public final ResponseEntity<RequestedResourceView> create(
                final RequestedResourceDesc desc) {
            throw new MethodNotAllowed();
        }
    }

    /**
     * Offers the endpoints for managing agreements.
     */
    @RestController
    @RequestMapping("/api/agreements")
    @Tag(name = ResourceName.AGREEMENTS, description = ResourceDescription.AGREEMENTS)
    public static class AgreementController extends BaseResourceController<Agreement, AgreementDesc,
            AgreementView, AgreementService> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = ResponseDescription.METHOD_NOT_ALLOWED)})
        public final ResponseEntity<AgreementView> create(final AgreementDesc desc) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = ResponseDescription.METHOD_NOT_ALLOWED)})
        public final ResponseEntity<AgreementView> update(@Valid final UUID resourceId,
                                                          final AgreementDesc desc) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = ResponseDescription.METHOD_NOT_ALLOWED)})
        public final ResponseEntity<Void> delete(@Valid final UUID resourceId) {
            throw new MethodNotAllowed();
        }
    }

    /**
     * Offers the endpoints for managing artifacts.
     */
    @RestController
    @RequestMapping("/api/artifacts")
    @Tag(name = ResourceName.ARTIFACTS, description = ResourceDescription.ARTIFACTS)
    @RequiredArgsConstructor
    public static class ArtifactController
            extends BaseResourceNotificationController<Artifact, ArtifactDesc, ArtifactView,
            ArtifactService> {

        /**
         * The service managing artifacts.
         */
        private final @NonNull ArtifactService artifactSvc;

        /**
         * The receiver for getting data from a remote source.
         */
        private final @NonNull BlockingArtifactReceiver dataReceiver;

        /**
         * The verifier for the data access.
         */
        private final @NonNull DataAccessVerifier accessVerifier;

        /**
         * Returns data from the local database or a remote data source. In case of a remote data
         * source, all headers and query parameters included in this request will be used for the
         * request to the backend.
         *
         * @param artifactId   Artifact id.
         * @param download     If the data should be forcefully downloaded.
         * @param agreementUri The agreement which should be used for access control.
         * @param params       All request parameters.
         * @param headers      All request headers.
         * @param request      The current http request.
         * @return The data object.
         * @throws IOException if the data cannot be received.
         */
        @GetMapping("{id}/data/**")
        @Operation(summary = "Get data by artifact id with query input")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.OK,
                description = ResponseDescription.OK)})
        public ResponseEntity<StreamingResponseBody> getData(
                @Valid @PathVariable(name = "id") final UUID artifactId,
                @RequestParam(required = false) final Boolean download,
                @RequestParam(required = false) final URI agreementUri,
                @RequestParam(required = false) final Map<String, String> params,
                @RequestHeader final Map<String, String> headers,
                final HttpServletRequest request) throws IOException {
            headers.remove("authorization");
            headers.remove("host");

            final var queryInput = new QueryInput();
            queryInput.setParams(params);
            queryInput.setHeaders(headers);

            final var searchString = request.getContextPath() + "/data";
            var optional = request.getRequestURI().substring(
                    request.getRequestURI().indexOf(searchString) + searchString.length());
            if ("/**".equals(optional)) {
                optional = "";
            }

            if (!optional.isBlank()) {
                queryInput.setOptional(optional);
            }

            /*
                If no agreement information has been passed the connector needs
                to check if the data access is restricted by the usage control.
             */
            final var data = (agreementUri == null)
                    ? artifactSvc.getData(accessVerifier, dataReceiver, artifactId, queryInput)
                    : artifactSvc.getData(accessVerifier, dataReceiver, artifactId,
                    new RetrievalInformation(agreementUri, download, queryInput));

            return returnData(artifactId, data);
        }

        /**
         * Returns data from the local database or a remote data source. In case of a remote data
         * source, the headers, query parameters and path variables from the request body will be
         * used when fetching the data.
         *
         * @param artifactId Artifact id.
         * @param queryInput Query input containing headers, query parameters, and path variables.
         * @return The data object.
         * @throws IOException                 if the data could not be stored.
         * @throws UnexpectedResponseException if the ids response message has been unexpected.
         */
        @PostMapping("{id}/data")
        @Operation(summary = "Get data by artifact id with query input")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.OK,
                description = ResponseDescription.OK)})
        public ResponseEntity<StreamingResponseBody> getData(
                @Valid @PathVariable(name = "id") final UUID artifactId,
                @RequestBody(required = false) final QueryInput queryInput)
                throws IOException,
                UnexpectedResponseException,
                io.dataspaceconnector.exception.UnexpectedResponseException {
            ValidationUtils.validateQueryInput(queryInput);
            final var data =
                    artifactSvc.getData(accessVerifier, dataReceiver, artifactId, queryInput);
            return returnData(artifactId, data);
        }

        private ResponseEntity<StreamingResponseBody> returnData(
                final UUID artifactId, final InputStream data) {
            final StreamingResponseBody body = outputStream -> {
                final int blockSize = 1024;
                int numBytesToWrite;
                var buffer = new byte[blockSize];
                while ((numBytesToWrite = data.read(buffer, 0, buffer.length)) != -1) {
                    outputStream.write(buffer, 0, numBytesToWrite);
                }

                data.close();
            };

            final var outputHeader = new HttpHeaders();
            outputHeader.set("Content-Disposition", "attachment;filename=" + artifactId.toString());

            return ResponseEntity.ok()
                    .headers(outputHeader)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(body);
        }

        /**
         * Replace the data of an artifact.
         *
         * @param artifactId  The artifact whose data should be replaced.
         * @param inputStream The new data.
         * @return Http Status ok.
         * @throws IOException if the data could not be stored.
         */
        @PutMapping(value = "{id}/data", consumes = "*/*")
        public ResponseEntity<Void> putData(
                @Valid @PathVariable(name = "id") final UUID artifactId,
                @RequestBody final byte[] inputStream) throws IOException {
            artifactSvc.setData(artifactId, new ByteArrayInputStream(inputStream));
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Offers the endpoints for managing subscriptions.
     */
    @RestController
    @RequestMapping("/api/subscriptions")
    @RequiredArgsConstructor
    @Tag(name = ResourceName.SUBSCRIPTIONS, description = ResourceDescription.SUBSCRIPTIONS)
    public static class SubscriptionController extends BaseResourceController<Subscription,
            SubscriptionDesc, SubscriptionView, SubscriptionService> {

        /**
         * The service for managing connector settings.
         */
        private final @NonNull ConnectorService connectorSvc;

        /**
         * Create subscription and set ids protocol value to false as this subscription has been
         * created via a REST API call.
         *
         * @param desc The resource description.
         * @return Response with code 201 (Created).
         */
        @Override
        @PostMapping
        @Operation(summary = "Create a base resource")
        @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created")})
        public ResponseEntity<SubscriptionView> create(@RequestBody final SubscriptionDesc desc) {
            // Set boolean to false as this subscription has been created via a REST API call.
            desc.setIdsProtocol(false);

            final var obj = getService().create(desc);
            final var entity = getAssembler().toModel(obj);

            final var headers = new HttpHeaders();
            headers.setLocation(entity.getRequiredLink("self").toUri());

            return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
        }

        /**
         * Get a list of all resources endpoints of subscription selected by a given filter.
         *
         * @param page The page index.
         * @param size The page size.
         * @return Response with code 200 (Ok) and the list of all endpoints of this resource type.
         */
        @GetMapping("owning")
        @SuppressWarnings("unchecked")
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final PagedModel<SubscriptionView> getAllFiltered(
                @RequestParam(required = false, defaultValue = "0") final Integer page,
                @RequestParam(required = false, defaultValue = "30") final Integer size) {
            final var pageable = Utils.toPageRequest(page, size);

            final var connectorId = connectorSvc.getConnectorId();
            final var list = getService().getBySubscriber(pageable, connectorId);

            final var entities = new PageImpl<>(list);
            PagedModel<SubscriptionView> model;
            if (entities.hasContent()) {
                model = getPagedAssembler().toModel(entities, getAssembler());
            } else {
                model = (PagedModel<SubscriptionView>) getPagedAssembler().toEmptyModel(entities,
                        getResourceType());
            }

            return model;
        }
    }
}
