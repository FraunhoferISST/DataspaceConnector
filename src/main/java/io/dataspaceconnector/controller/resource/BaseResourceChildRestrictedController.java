package io.dataspaceconnector.controller.resource;

import io.dataspaceconnector.controller.resource.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.resource.swagger.response.ResponseCode;
import io.dataspaceconnector.controller.resource.swagger.response.ResponseDescription;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.service.resource.RelationService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Offers methods for resource relation handling. Restricts access to modifying endpoints.
 *
 * @param <S> The service type for handling the relations logic.
 * @param <T> The type of the entity operated on.
 * @param <V> The type of the view model produces.
 */
public class BaseResourceChildRestrictedController<S extends RelationService<?, ?, ?, ?>,
        T extends Entity, V extends RepresentationModel<V>>
        extends BaseResourceChildController<S, T, V> {

    /**
     * {@inheritDoc}
     */
    @Hidden
    @Override
    @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
            description = ResponseDescription.METHOD_NOT_ALLOWED)})
    public PagedModel<V> addResources(
            @Valid @PathVariable(name = "id") final UUID ownerId,
            @Valid @RequestBody final List<URI> resources) {
        throw new MethodNotAllowed();
    }

    /**
     * {@inheritDoc}
     */
    @Hidden
    @Override
    @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
            description = ResponseDescription.METHOD_NOT_ALLOWED)})
    public HttpEntity<Void> replaceResources(@Valid @PathVariable(name = "id") final UUID ownerId,
                                             @Valid @RequestBody final List<URI> resources) {
        throw new MethodNotAllowed();
    }

    /**
     * {@inheritDoc}
     */
    @Hidden
    @Override
    @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
            description = ResponseDescription.METHOD_NOT_ALLOWED)})
    public HttpEntity<Void> removeResources(@Valid @PathVariable(name = "id") final UUID ownerId,
                                            @Valid @RequestBody final List<URI> resources) {
        throw new MethodNotAllowed();
    }
}
