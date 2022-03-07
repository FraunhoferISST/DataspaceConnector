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
package io.dataspaceconnector.service.resource.relation;

import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.service.resource.base.NonOwningRelationService;
import io.dataspaceconnector.service.resource.type.OfferedResourceService;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles the relation between representations and offered resources.
 */
@Service
@NoArgsConstructor
public class RepresentationOfferedResourceLinker extends NonOwningRelationService<Representation,
        OfferedResource, RepresentationService, OfferedResourceService> {

    @Override
    @SuppressWarnings("unchecked")
    protected final List<OfferedResource> getInternal(final Representation owner) {
        return (List<OfferedResource>) (List<?>) owner.getResources();
    }
}
