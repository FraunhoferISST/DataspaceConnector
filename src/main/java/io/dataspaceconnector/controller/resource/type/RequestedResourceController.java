package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceController;
import io.dataspaceconnector.controller.resource.base.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.resource.RequestedResourceView;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.service.resource.type.ResourceService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing requested resources.
 */
@RestController
@RequestMapping(BasePath.REQUESTS)
@RequiredArgsConstructor
@Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
public class RequestedResourceController extends BaseResourceController<RequestedResource,
        RequestedResourceDesc, RequestedResourceView, ResourceService<RequestedResource,
        RequestedResourceDesc>> {

    @Override
    @Hidden
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                    description = ResponseDescription.METHOD_NOT_ALLOWED)})
    public final ResponseEntity<RequestedResourceView> create(
            final RequestedResourceDesc desc) {
        throw new MethodNotAllowed();
    }
}
