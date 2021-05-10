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
package io.dataspaceconnector.view;

import java.util.UUID;

import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Helper for building self-links.
 */
public final class ViewAssemblerHelper {
    /**
     * Default constructor.
     */
    private ViewAssemblerHelper() {
        // Nothing to do here. Intentionally empty.
    }

    /**
     * Build self-link for entity.
     *
     * @param entityId The entity id.
     * @param tClass   The controller class for managing the entity class.
     * @param <T>      Type of the entity.
     * @return The self-link of the entity.
     * @throws IllegalArgumentException if the class is null.
     */
    public static <T> Link getSelfLink(final UUID entityId, final Class<T> tClass) {
        return linkTo(tClass).slash(entityId).withSelfRel();
    }
}
