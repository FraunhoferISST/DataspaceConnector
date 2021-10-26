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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.PagedModel;
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
@RequiredArgsConstructor
@RequestMapping(BasePath.SUBSCRIPTIONS)
@Tag(name = ResourceName.SUBSCRIPTIONS, description = ResourceDescription.SUBSCRIPTIONS)
public class SubscriptionController extends BaseResourceController<Subscription, SubscriptionDesc,
        SubscriptionView, SubscriptionService> {

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
    @Operation(summary = "Create a base resource.")
    @ApiResponse(responseCode = ResponseCode.CREATED, description = ResponseDescription.CREATED)
    public ResponseEntity<SubscriptionView> create(@RequestBody final SubscriptionDesc desc) {
        // Set boolean to false as this subscription has been created via a REST API call.
        desc.setIdsProtocol(false);
        return super.create(desc);
    }

    /**
     * Get a list of all resources endpoints of subscription selected by a given filter.
     *
     * @param page The page index.
     * @param size The page size.
     * @return Response with code 200 (Ok) and the list of all endpoints of this resource type.
     */
    @GetMapping("owning")
    @Operation(summary = "Get all subscriptions owned by this connector.")
    @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
            description = ResponseDescription.METHOD_NOT_ALLOWED)
    public final PagedModel<SubscriptionView> getAllFiltered(
            @RequestParam(required = false, defaultValue = "0") final Integer page,
            @RequestParam(required = false, defaultValue = "30") final Integer size) {
        final var pageable = Utils.toPageRequest(page, size);

        final var connectorId = connectorSvc.getConnectorId();
        final var list = Utils.toPage(getService()
                .getBySubscriber(pageable, connectorId), pageable);
        return getPagedModel(list);
    }
}
