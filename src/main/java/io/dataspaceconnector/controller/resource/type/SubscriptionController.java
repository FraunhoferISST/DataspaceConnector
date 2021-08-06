package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.subscription.SubscriptionView;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.service.resource.type.SubscriptionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing subscriptions.
 */
@RestController
@RequestMapping(BasePath.SUBSCRIPTIONS)
@RequiredArgsConstructor
@Tag(name = ResourceName.SUBSCRIPTIONS, description = ResourceDescription.SUBSCRIPTIONS)
public class SubscriptionController extends BaseResourceController<Subscription,
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.CREATED,
                    description = ResponseDescription.CREATED),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                    description = ResponseDescription.METHOD_NOT_ALLOWED),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
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
