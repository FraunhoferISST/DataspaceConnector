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
package io.dataspaceconnector.service.resource.spring;

import java.util.Optional;

import io.dataspaceconnector.common.runtime.ServiceResolver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Finds other services classes at runtime.
 */
@Service
@RequiredArgsConstructor
public class ServiceLookUp implements ServiceResolver {

    /**
     * Application context.
     */
    private final @NonNull ApplicationContext context;

    /** {@inheritDoc} */
    @Override
    public <T> Optional<T> getService(final Class<T> clazz) {
        try {
            return Optional.of(context.getBean(clazz));
        } catch (NoSuchBeanDefinitionException ignored) { }

        return Optional.empty();
    }
}
