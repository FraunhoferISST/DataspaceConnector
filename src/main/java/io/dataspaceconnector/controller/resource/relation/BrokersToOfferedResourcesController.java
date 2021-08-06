package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildRestrictedController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.resource.OfferedResourceView;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.service.resource.relation.BrokerOfferedResourceLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing the relations between broker and offered resources.
 */
@RestController
@RequestMapping(BasePath.BROKERS + "/{id}/" + BaseType.OFFERS)
@Tag(name = ResourceName.BROKERS, description = ResourceDescription.BROKERS)
public class BrokersToOfferedResourcesController extends BaseResourceChildRestrictedController<
        BrokerOfferedResourceLinker, OfferedResource, OfferedResourceView> {
}
