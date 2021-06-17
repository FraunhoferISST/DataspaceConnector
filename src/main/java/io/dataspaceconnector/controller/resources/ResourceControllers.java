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
package io.dataspaceconnector.controller.resources;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import io.dataspaceconnector.controller.resources.exceptions.MethodNotAllowed;
import io.dataspaceconnector.controller.resources.tags.ResourceDescriptions;
import io.dataspaceconnector.controller.resources.tags.ResourceNames;
import io.dataspaceconnector.model.core.Agreement;
import io.dataspaceconnector.model.core.AgreementDesc;
import io.dataspaceconnector.model.core.Artifact;
import io.dataspaceconnector.model.core.ArtifactDesc;
import io.dataspaceconnector.model.core.Catalog;
import io.dataspaceconnector.model.core.CatalogDesc;
import io.dataspaceconnector.model.core.Contract;
import io.dataspaceconnector.model.core.ContractDesc;
import io.dataspaceconnector.model.core.ContractRule;
import io.dataspaceconnector.model.core.ContractRuleDesc;
import io.dataspaceconnector.model.core.OfferedResource;
import io.dataspaceconnector.model.core.OfferedResourceDesc;
import io.dataspaceconnector.common.QueryInput;
import io.dataspaceconnector.model.core.Representation;
import io.dataspaceconnector.model.core.RepresentationDesc;
import io.dataspaceconnector.model.core.RequestedResource;
import io.dataspaceconnector.model.core.RequestedResourceDesc;
import io.dataspaceconnector.ids.BlockingArtifactReceiver;
import io.dataspaceconnector.services.resources.AgreementService;
import io.dataspaceconnector.services.resources.ArtifactService;
import io.dataspaceconnector.services.resources.CatalogService;
import io.dataspaceconnector.services.resources.ContractService;
import io.dataspaceconnector.services.resources.RepresentationService;
import io.dataspaceconnector.services.resources.ResourceService;
import io.dataspaceconnector.services.resources.RetrievalInformation;
import io.dataspaceconnector.services.resources.RuleService;
import io.dataspaceconnector.usagecontrol.DataAccessVerifier;
import io.dataspaceconnector.controller.resources.util.ValidationUtils;
import io.dataspaceconnector.controller.resources.view.AgreementView;
import io.dataspaceconnector.controller.resources.view.ArtifactView;
import io.dataspaceconnector.controller.resources.view.CatalogView;
import io.dataspaceconnector.controller.resources.view.ContractRuleView;
import io.dataspaceconnector.controller.resources.view.ContractView;
import io.dataspaceconnector.controller.resources.view.OfferedResourceView;
import io.dataspaceconnector.controller.resources.view.RepresentationView;
import io.dataspaceconnector.controller.resources.view.RequestedResourceView;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    @Tag(name = ResourceNames.CATALOGS, description = ResourceDescriptions.CATALOGS)
    public static class CatalogController
            extends BaseResourceController<Catalog, CatalogDesc, CatalogView, CatalogService> {
    }

    /**
     * Offers the endpoints for managing rules.
     */
    @RestController
    @RequestMapping("/api/rules")
    @Tag(name = ResourceNames.RULES, description = ResourceDescriptions.RULES)
    public static class RuleController extends BaseResourceController<ContractRule,
            ContractRuleDesc, ContractRuleView, RuleService> {
    }

    /**
     * Offers the endpoints for managing representations.
     */
    @RestController
    @RequestMapping("/api/representations")
    @Tag(name = ResourceNames.REPRESENTATIONS, description = ResourceDescriptions.REPRESENTATIONS)
    public static class RepresentationController extends BaseResourceController<Representation,
            RepresentationDesc, RepresentationView, RepresentationService> {
    }

    /**
     * Offers the endpoints for managing contracts.
     */
    @RestController
    @RequestMapping("/api/contracts")
    @Tag(name = ResourceNames.CONTRACTS, description = ResourceDescriptions.CONTRACTS)
    public static class ContractController
            extends BaseResourceController<Contract, ContractDesc, ContractView, ContractService> {
    }

    /**
     * Offers the endpoints for managing offered resources.
     */
    @RestController
    @RequestMapping("/api/offers")
    @Tag(name = ResourceNames.OFFERS, description = ResourceDescriptions.OFFERS)
    public static class OfferedResourceController
            extends BaseResourceController<OfferedResource, OfferedResourceDesc,
            OfferedResourceView, ResourceService<OfferedResource, OfferedResourceDesc>> {
    }

    /**
     * Offers the endpoints for managing requested resources.
     */
    @RestController
    @RequestMapping("/api/requests")
    @Tag(name = ResourceNames.REQUESTS, description = ResourceDescriptions.REQUESTS)
    public static class RequestedResourceController
            extends BaseResourceController<RequestedResource, RequestedResourceDesc,
            RequestedResourceView,
            ResourceService<RequestedResource, RequestedResourceDesc>> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
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
    @Tag(name = ResourceNames.AGREEMENTS, description = ResourceDescriptions.AGREEMENTS)
    public static class AgreementController extends BaseResourceController<Agreement, AgreementDesc,
            AgreementView, AgreementService> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final ResponseEntity<AgreementView> create(final AgreementDesc desc) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final ResponseEntity<Object> update(@Valid final UUID resourceId,
                                                   final AgreementDesc desc) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final ResponseEntity<Void> delete(@Valid final UUID resourceId) {
            throw new MethodNotAllowed();
        }
    }

    /**
     * Offers the endpoints for managing artifacts.
     */
    @RestController
    @RequestMapping("/api/artifacts")
    @Tag(name = ResourceNames.ARTIFACTS, description = ResourceDescriptions.ARTIFACTS)
    @RequiredArgsConstructor
    public static class ArtifactController
            extends BaseResourceController<Artifact, ArtifactDesc, ArtifactView, ArtifactService> {

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
        private final @NonNull
        DataAccessVerifier accessVerifier;

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
        @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
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
            // TODO: Check what happens when this connector is the provider and one of its provided
            //  agreements is passed.
            final var data = (agreementUri == null)
                    ? artifactSvc.getData(accessVerifier, dataReceiver, artifactId, queryInput)
                    : artifactSvc.getData(accessVerifier, dataReceiver, artifactId,
                    new RetrievalInformation(agreementUri, download,
                                             queryInput));

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
         * @throws IOException if the data could not be stored.
         */
        @PostMapping("{id}/data")
        @Operation(summary = "Get data by artifact id with query input")
        @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
        public ResponseEntity<StreamingResponseBody> getData(
                @Valid @PathVariable(name = "id") final UUID artifactId,
                @RequestBody(required = false) final QueryInput queryInput) throws IOException {
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
            return ResponseEntity.ok().build();
        }
    }
}
