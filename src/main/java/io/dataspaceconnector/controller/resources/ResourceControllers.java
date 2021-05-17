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

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.dataspaceconnector.model.Agreement;
import io.dataspaceconnector.model.AgreementDesc;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.model.ArtifactDesc;
import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.CatalogDesc;
import io.dataspaceconnector.model.Contract;
import io.dataspaceconnector.model.ContractDesc;
import io.dataspaceconnector.model.ContractRule;
import io.dataspaceconnector.model.ContractRuleDesc;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.OfferedResourceDesc;
import io.dataspaceconnector.model.QueryInput;
import io.dataspaceconnector.model.Representation;
import io.dataspaceconnector.model.RepresentationDesc;
import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.RequestedResourceDesc;
import io.dataspaceconnector.services.BlockingArtifactReceiver;
import io.dataspaceconnector.services.resources.AgreementService;
import io.dataspaceconnector.services.resources.ArtifactService;
import io.dataspaceconnector.services.resources.CatalogService;
import io.dataspaceconnector.services.resources.ContractService;
import io.dataspaceconnector.services.resources.RepresentationService;
import io.dataspaceconnector.services.resources.ResourceService;
import io.dataspaceconnector.services.resources.RetrievalInformation;
import io.dataspaceconnector.services.resources.RuleService;
import io.dataspaceconnector.services.usagecontrol.DataAccessVerifier;
import io.dataspaceconnector.utils.ValidationUtils;
import io.dataspaceconnector.view.AgreementView;
import io.dataspaceconnector.view.ArtifactView;
import io.dataspaceconnector.view.CatalogView;
import io.dataspaceconnector.view.ContractRuleView;
import io.dataspaceconnector.view.ContractView;
import io.dataspaceconnector.view.OfferedResourceView;
import io.dataspaceconnector.view.RepresentationView;
import io.dataspaceconnector.view.RequestedResourceView;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
    @Tag(name = "Catalogs", description = "Endpoints for CRUD operations on catalogs")
    public static class CatalogController
            extends BaseResourceController<Catalog, CatalogDesc, CatalogView, CatalogService> {
    }

    /**
     * Offers the endpoints for managing rules.
     */
    @RestController
    @RequestMapping("/api/rules")
    @Tag(name = "Rules", description = "Endpoints for CRUD operations on rules")
    public static class RuleController extends BaseResourceController<ContractRule,
            ContractRuleDesc, ContractRuleView, RuleService> {
    }

    /**
     * Offers the endpoints for managing representations.
     */
    @RestController
    @RequestMapping("/api/representations")
    @Tag(name = "Representations", description = "Endpoints for CRUD operations on representations")
    public static class RepresentationController extends BaseResourceController<Representation,
            RepresentationDesc, RepresentationView, RepresentationService> {
    }

    /**
     * Offers the endpoints for managing contracts.
     */
    @RestController
    @RequestMapping("/api/contracts")
    @Tag(name = "Contracts", description = "Endpoints for CRUD operations on contracts")
    public static class ContractController
            extends BaseResourceController<Contract, ContractDesc, ContractView, ContractService> {
    }

    /**
     * Offers the endpoints for managing offered resources.
     */
    @RestController
    @RequestMapping("/api/offers")
    @Tag(name = "Resources", description = "Endpoints for CRUD operations on offered resources")
    public static class OfferedResourceController
            extends BaseResourceController<OfferedResource, OfferedResourceDesc,
            OfferedResourceView, ResourceService<OfferedResource, OfferedResourceDesc>> {
    }

    /**
     * Offers the endpoints for managing requested resources.
     */
    @RestController
    @RequestMapping("/api/requests")
    @Tag(name = "Resources", description = "Endpoints for CRUD operations on requested resources")
    public static class RequestedResourceController
            extends BaseResourceController<RequestedResource, RequestedResourceDesc,
            RequestedResourceView,
            ResourceService<RequestedResource, RequestedResourceDesc>> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final ResponseEntity<RequestedResourceView> create(
                final RequestedResourceDesc desc) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    /**
     * Offers the endpoints for managing agreements.
     */
    @RestController
    @RequestMapping("/api/agreements")
    @Tag(name = "Agreements", description = "Endpoints for contract/policy handling")
    public static class AgreementController extends BaseResourceController<Agreement, AgreementDesc,
            AgreementView, AgreementService> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final ResponseEntity<AgreementView> create(final AgreementDesc desc) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final ResponseEntity<Object> update(@Valid final UUID resourceId,
                                                   final AgreementDesc desc) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final ResponseEntity<Void> delete(@Valid final UUID resourceId) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    /**
     * Offers the endpoints for managing artifacts.
     */
    @RestController
    @RequestMapping("/api/artifacts")
    @Tag(name = "Artifacts", description = "Endpoints for CRUD operations on artifacts")
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
         */
        @GetMapping("{id}/data/**")
        @Operation(summary = "Get data by artifact id with query input")
        @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
        public ResponseEntity<StreamingResponseBody> getData(
                @Valid @PathVariable(name = "id") final UUID artifactId,
                @RequestParam(required = false) final Boolean download,
                @RequestParam(required = false) final URI agreementUri,
                @RequestParam final Map<String, String> params,
                @RequestHeader final Map<String, String> headers,
                final HttpServletRequest request) {
            headers.remove("authorization");
            headers.remove("host");

            final var queryInput = new QueryInput();
            queryInput.setParams(params);
            queryInput.setHeaders(headers);

            final var searchString = request.getContextPath() + "/data";
            final var optional = request.getRequestURI().substring(
                    request.getRequestURI().indexOf(searchString) + searchString.length());

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

            StreamingResponseBody body = outputStream -> {
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
         * Returns data from the local database or a remote data source. In case of a remote data
         * source, the headers, query parameters and path variables from the request body will be
         * used when fetching the data.
         *
         * @param artifactId Artifact id.
         * @param queryInput Query input containing headers, query parameters, and path variables.
         * @return The data object.
         */
        @PostMapping("{id}/data")
        @Operation(summary = "Get data by artifact id with query input")
        @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
        public ResponseEntity<Object> getData(
                @Valid @PathVariable(name = "id") final UUID artifactId,
                @RequestBody(required = false) final QueryInput queryInput) {
            ValidationUtils.validateQueryInput(queryInput);
            return ResponseEntity.ok(artifactSvc.getData(accessVerifier, dataReceiver, artifactId,
                    queryInput));
        }

        /**
         * Replace the data of an artifact.
         *
         * @param artifactId  The artifact whose data should be replaced.
         * @param inputStream The new data.
         * @return Http Status ok.
         */
        @PutMapping(value = "{id}/data", consumes = "*/*")
        public ResponseEntity<Void> putData(
                @Valid @PathVariable(name = "id") final UUID artifactId,
                @RequestBody final byte[] inputStream) {
            artifactSvc.setData(artifactId, new ByteArrayInputStream(inputStream));
            return ResponseEntity.ok().build();
        }
    }
}
