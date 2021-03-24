package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.RequestedResourceView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Resources", description = "Endpoints for CRUD operations on requested resources")
public class RequestedResourceController extends BaseResourceController<RequestedResource, RequestedResourceDesc, RequestedResourceView, ResourceService<RequestedResource, RequestedResourceDesc>> {
    @Override
    @Hidden
    @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
    public HttpEntity<RequestedResourceView> create(final RequestedResourceDesc desc) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
