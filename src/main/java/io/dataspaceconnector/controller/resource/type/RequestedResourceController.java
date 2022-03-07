/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing requested resources.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(BasePath.REQUESTS)
@Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
public class RequestedResourceController extends BaseResourceController<RequestedResource,
        RequestedResourceDesc, RequestedResourceView, ResourceService<RequestedResource,
        RequestedResourceDesc>> {

    @Hidden
    @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
            description = ResponseDescription.METHOD_NOT_ALLOWED)
    @Override
    public final ResponseEntity<RequestedResourceView> create(
            final RequestedResourceDesc desc) {
        throw new MethodNotAllowed();
    }
}
