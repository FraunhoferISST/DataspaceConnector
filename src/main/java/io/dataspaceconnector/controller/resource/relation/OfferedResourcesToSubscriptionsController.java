package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildRestrictedController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.subscription.SubscriptionView;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.service.resource.relation.OfferedResourceSubscriptionLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing the relations between offered resources and subscriptions.
 */
@RestController
@RequestMapping(BasePath.OFFERS + "/{id}/" + BaseType.SUBSCRIPTIONS)
@Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
public class OfferedResourcesToSubscriptionsController
        extends BaseResourceChildRestrictedController<OfferedResourceSubscriptionLinker,
        Subscription, SubscriptionView> {
}
