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
package io.dataspaceconnector.service.resource.type;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.resource.OfferedResourceFactory;
import io.dataspaceconnector.repository.BaseEntityRepository;

/**
 * Handles the basic logic for offered resources.
 */
public class OfferedResourceService extends ResourceService<OfferedResource, OfferedResourceDesc> {

    /**
     * Constructor.
     *
     * @param repository The offered resource repository.
     */
    @SuppressFBWarnings("MC_OVERRIDABLE_METHOD_CALL_IN_CONSTRUCTOR")
    public OfferedResourceService(final BaseEntityRepository<OfferedResource> repository) {
        this(repository, new OfferedResourceFactory());
        ((OfferedResourceFactory) this.getFactory()).setDoesExist(super::doesExist);
    }

    /**
     * Constructor.
     *
     * @param repository The offered resource repository.
     * @param factory    The offered resource factory.
     */
    public OfferedResourceService(
            final BaseEntityRepository<OfferedResource> repository,
            final AbstractFactory<OfferedResource, OfferedResourceDesc> factory) {
        super(repository, factory);
    }
}
