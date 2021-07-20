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
package io.dataspaceconnector.controller.configuration;

import io.dataspaceconnector.controller.resource.BaseResourceChildController;
import io.dataspaceconnector.controller.resource.BaseResourceController;
import io.dataspaceconnector.controller.resource.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.resource.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.OfferedResourceView;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.broker.BrokerDesc;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.service.configuration.BrokerService;
import io.dataspaceconnector.service.configuration.EntityLinkerService;
import io.dataspaceconnector.view.broker.BrokerView;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Controller for the broker entities.
 */
public final class BrokerControllers {

    /**
     * Offers the endpoints for managing brokers.
     */
    @RestController
    @RequestMapping("/api/brokers")
    @Tag(name = ResourceName.BROKERS, description = ResourceDescription.BROKERS)
    public static class BrokerController extends BaseResourceController<Broker, BrokerDesc,
            BrokerView, BrokerService> {
    }

    /**
     * Offers the endpoints for managing the relations between broker and offered resources.
     */
    @RestController
    @RequestMapping("/api/brokers/{id}/offers")
    @Tag(name = ResourceName.BROKERS, description = ResourceDescription.BROKERS)
    public static class BrokerToOfferedResources extends
            BaseResourceChildController<EntityLinkerService.BrokerOfferedResourcesLinker,
                    OfferedResource, OfferedResourceView> {
        @Override
        @Hidden
        @ApiResponses(value = {
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "405", description = "Not allowed")})
        public final PagedModel<OfferedResourceView> addResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "405", description = "No content")})
        public final HttpEntity<Void> replaceResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "405", description = "No content")})
        public final HttpEntity<Void> removeResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }
    }
}
