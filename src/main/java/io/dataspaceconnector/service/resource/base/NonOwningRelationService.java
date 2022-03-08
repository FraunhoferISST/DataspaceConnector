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
package io.dataspaceconnector.service.resource.base;

import io.dataspaceconnector.model.base.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/*
    NOTE: All entities in our model use n to m relationships. Due to the use of JPA
    only one entity can be the owner of the relationship and only the owner may persist
    changes to the relationship. This means when both sides should be capable of persisting
    the CASCADING Annotation needs to be used. Here lies the problem. When getting an entity
    and all related entities you could modify its children. With the missing setter only
    the Factories are capable of changing but you could still change the children. And
    the decision if an entity should be persisted should only be with the Service responsible
    for the specific entity.
    Due to this the ownership of the relationship leaks into the class design. The upside
    is that the leakage stops here and you only need to care about the different linker
    if you are adding or removing relationships.
    The following class is basically a proxy class calling the right RelationshipService.
 */

/**
 * Creates a parent-children relationship between two types of resources.
 * Implements the non-owning side of a relationship.
 * @param <K> The type of the parent resource.
 * @param <W> The type of the child resource. (The owning side)
 * @param <T> The service type for the parent resource.
 * @param <X> The service type for the child resource.
 */
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public abstract class NonOwningRelationService<
        K extends Entity,
        W extends Entity,
        T extends BaseEntityService<K, ?>,
        X extends BaseEntityService<W, ?>
        > extends AbstractRelationService<K, W, T, X> {

    /*
       NOTE: For some reason Spring does not find the owningService when the services are
       set. As long this does not create a problem, do not touch this.
     */
    /**
     * The service response for the inverse of this relation.
     */
    @Autowired
    private OwningRelationService<W, K, ?, ?> owningService;

    @Override
    protected final void addInternal(final UUID ownerId, final Set<UUID> entities) {
        final var set = Set.of(ownerId);
        entities.forEach(id -> owningService.add(id, set));
    }

    @Override
    public final void removeInternal(final UUID ownerId, final Set<UUID> entities) {
        final var set = Set.of(ownerId);
        for (final var id : entities) {
            owningService.remove(id, set);
        }
    }

    @Override
    public final void replaceInternal(final UUID ownerId, final Set<UUID> entities) {
        final var set = Set.of(ownerId);
        final var allRelations =
                getOneService().getAll(Pageable.unpaged()).stream().map(Entity::getId)
                               .collect(Collectors.toList());

        for (final var id : allRelations) {
            owningService.remove(id, set);
        }

        add(ownerId, entities);
    }
}
