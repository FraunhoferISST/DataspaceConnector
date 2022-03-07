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
package io.dataspaceconnector.service.resource.ids.builder.base;

import de.fraunhofer.iais.eis.ModelClass;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.base.Entity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The base class for constructing an ids object from DSC objects.
 *
 * @param <T> The type of the DSC object.
 * @param <X> The type of the ids object.
 */
@RequiredArgsConstructor
public abstract class AbstractIdsBuilder<T extends Entity, X extends ModelClass> {

    /**
     * The default depth the builder will follow dependencies.
     */
    public static final int DEFAULT_DEPTH = -1;

    /**
     * Helper for creating self links.
     */
    private final @NonNull SelfLinkHelper selfLinkHelper;

    /**
     * Convert a DSC object to an ids object. The default depth will be used to determine the
     * when to stop following dependencies.
     *
     * @param entity The entity to be converted.
     * @return The ids object.
     */
    public X create(final T entity) throws ConstraintViolationException {
        return create(entity, DEFAULT_DEPTH);
    }

    /**
     * Convert a DSC object to an ids object.
     *
     * @param entity   The entity to be converted.
     * @param maxDepth The depth determines when to stop following dependencies. Set this value to a
     *                 negative number to follow all dependencies.
     * @return The ids object.
     */
    public X create(final T entity, final int maxDepth) throws ConstraintViolationException {
        return create(entity, 0, maxDepth);
    }

    private X create(final T entity, final int currentDepth,
                     final int maxDepth) throws ConstraintViolationException {
        final var resource = createInternal(entity, currentDepth, maxDepth);
        if (resource != null) {
            return addAdditionals(resource, entity.getAdditional());
        } else {
            return null;
        }
    }

    /**
     * This is the type specific call for converting a DSC object to an ids object. The additional
     * field will be set automatically.
     *
     * @param entity       The entity to be converted.
     * @param currentDepth The current distance to the original call.
     * @param maxDepth     The max depth to the original call.
     * @return The ids object.
     */
    protected abstract X createInternal(T entity, int currentDepth, int maxDepth)
            throws ConstraintViolationException;

    /**
     * Use this function to construct the absolute path to this entity.
     *
     * @param entity The entity.
     * @param <K>    The entity type.
     * @return The absolute path to this entity.
     */
    protected <K extends Entity> URI getAbsoluteSelfLink(final K entity) {
        return selfLinkHelper.getSelfLink(entity);
    }

    private static boolean shouldGenerate(final int currentDepth, final int maxDepth) {
        return currentDepth <= maxDepth || maxDepth < 0;
    }

    /**
     * Batch call of create. Use this call for building an object's dependencies. This function
     * increments the currentDepth.
     *
     * @param builder      The builder applied to all objects.
     * @param entityList   The entities that need to be converted.
     * @param currentDepth The current distance to the original call.
     * @param maxDepth     The distance to the original call.
     * @param <V>          The type of the DSC entity.
     * @param <W>          The type of the ids entity.
     * @return The converted ids objects. Null if the distance is too far from the original call.
     */
    protected <V extends Entity, W extends ModelClass> Optional<List<W>> create(
            final AbstractIdsBuilder<V, W> builder, final List<V> entityList,
            final int currentDepth, final int maxDepth) throws ConstraintViolationException {
        final int nextDepth = currentDepth + 1;

        return !shouldGenerate(nextDepth, maxDepth) ? Optional.empty()
                : Optional.of(Utils.toStream(entityList)
                .map(r -> builder.create(r, nextDepth, maxDepth))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    private <K extends ModelClass> K addAdditionals(final K idsObject,
                                                    final Map<String, String> additional) {
        for (final var entry : additional.entrySet()) {
            idsObject.setProperty(entry.getKey(), entry.getValue());
        }

        return idsObject;
    }
}
