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
package io.dataspaceconnector.service.resource.ids.updater;

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.base.Entity;

/**
 * Updates an DSC object by providing an IDS object.
 * @param <I> The IDS object type.
 * @param <O> The DSC object type.
 */
public interface InfomodelUpdater<I, O extends Entity> {
    /**
     * Update an entity that is known to the consumer.
     * @param entity The ids object.
     * @return The updated dsc object.
     */
    O update(I entity) throws ResourceNotFoundException;
}
