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
package io.dataspaceconnector.controller.resource.view.proxy;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.net.URI;
import java.util.List;

/**
 * The view class for the proxy.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ProxyView extends RepresentationModel<ProxyView> {

    /**
     * The location information.
     */
    private URI location;

    /**
     * The list of exclusions.
     */
    private List<String> exclusions;

    /**
     * Boolean value indicating whether auth-credentials for the proxy are present.
     */
    private boolean authenticationSet;
}
