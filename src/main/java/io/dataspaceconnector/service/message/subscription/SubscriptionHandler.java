package io.dataspaceconnector.service.message.subscription;

import io.dataspaceconnector.camel.exception.SubscriptionProcessingException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.AbstractEntity;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.Representation;
import io.dataspaceconnector.model.Subscription;
import io.dataspaceconnector.model.SubscriptionDesc;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.resource.RelationServices;
import io.dataspaceconnector.service.resource.SubscriptionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Set;

@Log4j2
@RequiredArgsConstructor
@Service
public class SubscriptionHandler {

    /**
     * Service for handling subscriptions.
     */
    private final @NonNull SubscriptionService subscriptionSvc;

    /**
     * Service for linking artifacts and subscriptions.
     */
    private final @NonNull RelationServices.ArtifactSubscriptionLinker artSubLinker;

    /**
     * Service for linking representations and subscriptions.
     */
    private final @NonNull RelationServices.RepresentationSubscriptionLinker repSubLinker;

    /**
     * Service for linking offered resources and subscriptions.
     */
    private final @NonNull RelationServices.OfferedResourceSubscriptionLinker offerSubLinker;

    /**
     * Service for linking requested resources and subscriptions.
     */
    private final @NonNull RelationServices.RequestedResourceSubscriptionLinker reqSubLinker;

    /**
     * Service for resolving database entities by id.
     */
    private final @NonNull EntityResolver entityResolver;

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
        // Check if target exists.
        final var target = subscription.getTarget();
        AbstractEntity entity;
        try {
            entity = entityResolver.getEntityById(target);
        } catch (Exception exception) {
            throw new ResourceNotFoundException("Element with target id could not be found.");
        }

        // Create new subscription.
        final var desc = new SubscriptionDesc();
        desc.setSubscriber(subscription.getSubscriber());
        desc.setTarget(subscription.getTarget());
        desc.setPushData(subscription.isPushData());
        desc.setUrl(subscription.getUrl());

        final var id = subscriptionSvc.create(desc).getId();

        // Link subscription to target.
        if (entity instanceof Artifact) {
            artSubLinker.add(entity.getId(), Set.of(id));
        } else if (entity instanceof Representation) {
            repSubLinker.add(entity.getId(), Set.of(id));
        } else if (entity instanceof OfferedResource) {
            offerSubLinker.add(entity.getId(), Set.of(id));
        } else {
            throw new SubscriptionProcessingException("No subscription offered for this target.");
        }
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
}
