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

import java.net.URI;
import java.util.List;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.exception.SubscriptionProcessingException;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.repository.SubscriptionRepository;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.resource.base.BaseEntityService;
import io.dataspaceconnector.service.resource.relation.ArtifactSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.OfferedResourceSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.RepresentationArtifactLinker;
import io.dataspaceconnector.service.resource.relation.RequestedResourceSubscriptionLinker;
import io.dataspaceconnector.service.resource.spring.ServiceLookUp;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

/**
 * Handles the basic logic for subscriptions.
 */
public class SubscriptionService extends BaseEntityService<Subscription, SubscriptionDesc> {

    /**
     * Service for resolving database entities by id.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for service lookup.
     */
    private final @NonNull ServiceLookUp lookUp;

    /**
     * Constructor.
     * @param repository The subscription repository.
     * @param factory    The subscription factory.
     * @param resolver   The entity resolver.
     * @param serviceLookUp The service lookup.
     */
    public SubscriptionService(
            final BaseEntityRepository<Subscription> repository,
            final AbstractFactory<Subscription, SubscriptionDesc> factory,
            final @NonNull EntityResolver resolver,
            final @NonNull ServiceLookUp serviceLookUp) {
        super(repository, factory);
        this.entityResolver = resolver;
        this.lookUp = serviceLookUp;
    }

    /**
     * @param desc The description of the new entity.
     * @return The created subscription.
     */
    @Override
    public Subscription create(final SubscriptionDesc desc) {
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        final var subscription = persist(getFactory().create(desc));
        final var target = subscription.getTarget();

        linkSubscriptionToEntityById(target, subscription);

        return subscription;
    }

    /**
     * Get a list of all subscriptions with a matching subscriber.
     *
     * @param pageable   Range selection of the complete data set.
     * @param subscriber The subscriber id.
     * @return The id list of all entities.
     * @throws IllegalArgumentException if a passed parameter is null.
     */
    public List<Subscription> getBySubscriber(final Pageable pageable, final URI subscriber) {
        Utils.requireNonNull(pageable, ErrorMessage.PAGEABLE_NULL);
        Utils.requireNonNull(subscriber, ErrorMessage.ENTITYID_NULL);
        return ((SubscriptionRepository) getRepository()).findAllBySubscriber(subscriber);
    }

    /**
     * Get a list if all subscriptions with a matching subscriber and target.
     *
     * @param subscriber The subscriber id.
     * @param target     The target id.
     * @return The id list of all entities.
     * @throws IllegalArgumentException if a passed parameter is null.
     */
    public List<Subscription> getBySubscriberAndTarget(final URI subscriber, final URI target) {
        Utils.requireNonNull(subscriber, ErrorMessage.ENTITYID_NULL);
        Utils.requireNonNull(target, ErrorMessage.ENTITYID_NULL);
        return ((SubscriptionRepository) getRepository()).findAllBySubscriberAndTarget(subscriber,
                target);
    }

    /**
     * Get a list of all subscriptions with a matching target.
     *
     * @param target The target id.
     * @return The id list of all entities.
     * @throws IllegalArgumentException if a passed parameter is null.
     */
    public List<Subscription> getByTarget(final URI target) {
        Utils.requireNonNull(target, ErrorMessage.ENTITYID_NULL);
        return ((SubscriptionRepository) getRepository()).findAllByTarget(target);
    }

    /**
     * Remove subscription with given target id and issuer uri.
     *
     * @param target The target id.
     * @param issuer The issuer connector/system.
     * @throws SubscriptionProcessingException if the subscription could not be removed.
     * @throws ResourceNotFoundException       if not matching subscription could be found.
     */
    @SuppressFBWarnings(
            value = "REC_CATCH_EXCEPTION",
            justification = "caught exceptions are unchecked"
    )
    public void removeSubscription(final URI target, final URI issuer)
            throws SubscriptionProcessingException, ResourceNotFoundException {
        final var subscriptions = getBySubscriberAndTarget(issuer, target);
        if (subscriptions.isEmpty()) {
            throw new ResourceNotFoundException("Subscription with the given target id for this"
                    + "issuer connector could not be found.");
        }

        try {
            for (final var subscription : subscriptions) {
                final var id = subscription.getId();
                delete(id);
            }
        } catch (Exception exception) {
            throw new SubscriptionProcessingException("Failed to remove subscription.");
        }
    }

    /**
     * Link subscription to database entity.
     *
     * @param target       The entity id.
     * @param subscription The subscription.
     * @throws SubscriptionProcessingException if subscription for targeted entity failed.
     * @throws ResourceNotFoundException       if the resource could not be found.
     */
    private void linkSubscriptionToEntityById(final URI target, final Subscription subscription)
            throws SubscriptionProcessingException, ResourceNotFoundException,
            IllegalArgumentException {
        // Check if target exists.
        final var entity = entityResolver.getEntityById(target);
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessage.EMTPY_ENTITY.toString());
        }

        // Link subscription to entity.
        final var subscriptionId = subscription.getId();
        final var value = entity.get();

        if (value instanceof Artifact) {
            lookUp.getService(ArtifactSubscriptionLinker.class).get()
                  .add(value.getId(), Set.of(subscriptionId));
        } else if (value instanceof Representation) {
            lookUp.getService(RepresentationArtifactLinker.class).get()
                  .add(value.getId(), Set.of(subscriptionId));
        } else if (value instanceof OfferedResource) {
            lookUp.getService(OfferedResourceSubscriptionLinker.class).get()
                  .add(value.getId(), Set.of(subscriptionId));
        } else if (value instanceof RequestedResource) {
            lookUp.getService(RequestedResourceSubscriptionLinker.class).get()
                  .add(value.getId(), Set.of(subscriptionId));
        } else {
            throw new SubscriptionProcessingException("No subscription offered for this target.");
        }
    }
}
