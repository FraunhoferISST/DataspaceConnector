package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.broker.BrokerView;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.broker.BrokerDesc;
import io.dataspaceconnector.service.resource.type.BrokerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing brokers.
 */
@RestController
@RequestMapping(BasePath.BROKERS)
@Tag(name = ResourceName.BROKERS, description = ResourceDescription.BROKERS)
public class BrokerController extends BaseResourceController<Broker, BrokerDesc, BrokerView,
        BrokerService> {
}
