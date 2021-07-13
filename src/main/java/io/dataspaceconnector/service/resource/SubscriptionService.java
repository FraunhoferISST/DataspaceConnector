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
package io.dataspaceconnector.service.resource;

import io.dataspaceconnector.camel.exception.SubscriptionProcessingException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.AbstractEntity;
import io.dataspaceconnector.model.AbstractFactory;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.Representation;
import io.dataspaceconnector.model.Subscription;
import io.dataspaceconnector.model.SubscriptionDesc;
import io.dataspaceconnector.repository.SubscriptionRepository;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.util.ErrorMessages;
import io.dataspaceconnector.util.Utils;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * Handles the basic logic for subscriptions.
 */
@Service
@NoArgsConstructor
public class SubscriptionService extends BaseEntityService<Subscription, SubscriptionDesc> {

    /**
     * Factory handling subscription changes.
     */
    @Autowired
    private AbstractFactory<Subscription, SubscriptionDesc> factory;

    /**
     * Service for handling subscriptions.
     */
    @Autowired
    private SubscriptionService subscriptionSvc;

    /**
     * Service for linking artifacts and subscriptions.
     */
    @Autowired
    private RelationServices.ArtifactSubscriptionLinker artSubLinker;

    /**
     * Service for linking representations and subscriptions.
     */
    @Autowired
    private RelationServices.RepresentationSubscriptionLinker repSubLinker;

    /**
     * Service for linking offered resources and subscriptions.
     */
    @Autowired
    private RelationServices.OfferedResourceSubscriptionLinker offerSubLinker;

    /**
     * Service for linking requested resources and subscriptions.
     */
    @Autowired
    private RelationServices.RequestedResourceSubscriptionLinker reqSubLinker;

    /**
     * Service for resolving database entities by id.
     */
    @Autowired
    private EntityResolver entityResolver;

    /**
     * @param desc The description of the new entity.
     * @return The created subscription.
     */
    @Override
    public Subscription create(final SubscriptionDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var subscription = persist(factory.create(desc));
        final var target = subscription.getTarget();

        try {
            linkSubscriptionToEntityById(target, subscription);
        } catch (Exception exception) {
            throw new ResourceNotFoundException("Failed to add subscription to target entity.");
        }

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
        Utils.requireNonNull(pageable, ErrorMessages.PAGEABLE_NULL);
        Utils.requireNonNull(subscriber, ErrorMessages.ENTITYID_NULL);
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
        Utils.requireNonNull(subscriber, ErrorMessages.ENTITYID_NULL);
        Utils.requireNonNull(target, ErrorMessages.ENTITYID_NULL);
        return ((SubscriptionRepository) getRepository()).findAllBySubscriberAndTarget(subscriber,
                target);
    }

    /**
     * Remove subscription with given target id and issuer uri.
     *
     * @param target The target id.
     * @param issuer The issuer connector/system.
     * @throws SubscriptionProcessingException if the subscription could not be removed.
     * @throws ResourceNotFoundException       if not matching subscription could be found.
     */
    public void removeSubscription(final URI target, final URI issuer)
            throws SubscriptionProcessingException, ResourceNotFoundException {
        final var subscriptions = subscriptionSvc.getBySubscriberAndTarget(issuer, target);
        if (subscriptions.isEmpty()) {
            throw new ResourceNotFoundException("Subscription with the given target id for this"
                    + "issuer connector could not be found.");
        }

        try {
            for (final var subscription : subscriptions) {
                final var id = subscription.getId();
                subscriptionSvc.delete(id);
            }
        } catch (Exception exception) {
            throw new SubscriptionProcessingException("Failed to remove subscription.");
        }
    }

    /**
     * Add subscription to the database and link it to the target entity.
     *
     * @param subscription The subscription.
     * @throws SubscriptionProcessingException                        if subscription for
     *                                                                targeted entity failed.
     * @throws ResourceNotFoundException                              if the resource could not
     *                                                                be found.
     * @throws io.dataspaceconnector.exception.InvalidEntityException if no valid entity could be
     *                                                                created.
     */
    public void addSubscription(final Subscription subscription)
            throws SubscriptionProcessingException, ResourceNotFoundException {
        // Create new subscription.
        final var desc = new SubscriptionDesc();
        final var target = subscription.getTarget();
        desc.setSubscriber(subscription.getSubscriber());
        desc.setTarget(target);
        desc.setPushData(subscription.isPushData());
        desc.setUrl(subscription.getUrl());

        // Create subscription and link it to target.
        linkSubscriptionToEntityById(target, subscriptionSvc.create(desc));
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
            throws SubscriptionProcessingException, ResourceNotFoundException {
        // Check if target exists.
        AbstractEntity entity;
        try {
            entity = entityResolver.getEntityById(target);
        } catch (Exception exception) {
            throw new ResourceNotFoundException("Element with target id could not be found.");
        }

        // Link subscription to entity.
        final var subscriptionId = subscription.getId();
        if (entity instanceof Artifact) {
            artSubLinker.add(entity.getId(), Set.of(subscriptionId));
        } else if (entity instanceof Representation) {
            repSubLinker.add(entity.getId(), Set.of(subscriptionId));
        } else if (entity instanceof OfferedResource) {
            offerSubLinker.add(entity.getId(), Set.of(subscriptionId));
        } else {
            throw new SubscriptionProcessingException("No subscription offered for this target.");
        }
    }
}
