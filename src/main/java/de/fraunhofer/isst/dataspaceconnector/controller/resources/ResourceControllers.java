package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.CatalogService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ContractService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RuleService;
import de.fraunhofer.isst.dataspaceconnector.utils.ValidationUtils;
import de.fraunhofer.isst.dataspaceconnector.view.AgreementView;
import de.fraunhofer.isst.dataspaceconnector.view.ArtifactView;
import de.fraunhofer.isst.dataspaceconnector.view.CatalogView;
import de.fraunhofer.isst.dataspaceconnector.view.ContractRuleView;
import de.fraunhofer.isst.dataspaceconnector.view.ContractView;
import de.fraunhofer.isst.dataspaceconnector.view.OfferedResourceView;
import de.fraunhofer.isst.dataspaceconnector.view.RepresentationView;
import de.fraunhofer.isst.dataspaceconnector.view.RequestedResourceView;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

public final class ResourceControllers {

    @RestController
    @RequestMapping("/api/catalogs")
    @Tag(name = "Catalogs", description = "Endpoints for CRUD operations on catalogs")
    public static class CatalogController extends BaseResourceController<Catalog, CatalogDesc,
            CatalogView, CatalogService> {
    }

    @RestController
    @RequestMapping("/api/rules")
    @Tag(name = "Rules", description = "Endpoints for CRUD operations on rules")
    public static class RuleController extends BaseResourceController<ContractRule,
            ContractRuleDesc, ContractRuleView, RuleService> {
    }

    @RestController
    @RequestMapping("/api/representations")
    @Tag(name = "Representations", description = "Endpoints for CRUD operations on representations")
    public static class RepresentationController extends BaseResourceController<Representation,
            RepresentationDesc, RepresentationView, RepresentationService> {
    }

    @RestController
    @RequestMapping("/api/contracts")
    @Tag(name = "Contracts", description = "Endpoints for CRUD operations on contracts")
    public static class ContractController extends BaseResourceController<Contract, ContractDesc,
            ContractView, ContractService> {
    }

    @RestController
    @RequestMapping("/api/offers")
    @Tag(name = "Resources", description = "Endpoints for CRUD operations on offered resources")
    public static class OfferedResourceController extends BaseResourceController<OfferedResource,
            OfferedResourceDesc, OfferedResourceView, ResourceService<OfferedResource,
            OfferedResourceDesc>> {
    }

    @RestController
    @RequestMapping("/api/requests")
    @Tag(name = "Resources", description = "Endpoints for CRUD operations on requested resources")
    public static class RequestedResourceController extends BaseResourceController<RequestedResource,
            RequestedResourceDesc, RequestedResourceView, ResourceService<RequestedResource,
            RequestedResourceDesc>> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public ResponseEntity<RequestedResourceView> create(final RequestedResourceDesc desc) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @RestController
    @RequestMapping("/api/agreements")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    public static class AgreementController extends BaseResourceController<Agreement,
            AgreementDesc, AgreementView, AgreementService> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public ResponseEntity<AgreementView> create(final AgreementDesc desc) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public ResponseEntity<Object> update(@Valid final UUID resourceId,
                                             final AgreementDesc desc) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public ResponseEntity<Void> delete(@Valid final UUID resourceId) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @RestController
    @RequestMapping("/api/artifacts")
    @Tag(name = "Artifacts", description = "Endpoints for CRUD operations on artifacts")
    public static class ArtifactController extends BaseResourceController<Artifact, ArtifactDesc,
            ArtifactView,
            ArtifactService> {

        /**
         * Returns data from the local database or a remote data source. In case of a remote data
         * source, all headers and query parameters included in this request will be used for the
         * request to the backend.
         *
         * @param artifactId Artifact id.
         * @param params     All request parameters.
         * @param headers    All request headers.
         * @return The data object.
         */
        @GetMapping("{id}/data/**")
        @Operation(summary = "Get data by artifact id with query input")
        @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
        public ResponseEntity<Object> getData(@Valid @PathVariable(name = "id") final UUID artifactId,
                                              @RequestParam(required = false) final Boolean download,
                                              @RequestParam final Map<String, String> params,
                                              @RequestHeader final Map<String, String> headers,
                                              final HttpServletRequest request) {
            final var optionalPath = request.getRequestURI()
                    .split(request.getContextPath() + "/data/")[1];

            headers.remove("authorization");
            headers.remove("host");

            final var queryInput = new QueryInput();
            queryInput.setParams(params);
            queryInput.setHeaders(headers);
            queryInput.setOptional(optionalPath);

            final var artifactService = this.getService();
            // TODO Forward download boolean
            return ResponseEntity.ok(artifactService.getData(artifactId, queryInput));
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
        public ResponseEntity<Object> getData(@Valid @PathVariable(name = "id") final UUID artifactId,
                                              @RequestBody(required = false) QueryInput queryInput) {
            final var artifactService = this.getService();

            ValidationUtils.validateQueryInput(queryInput);

            return ResponseEntity.ok(artifactService.getData(artifactId, queryInput));
        }
    }
}
