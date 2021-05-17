package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.Broker;
import io.dataspaceconnector.model.BrokerDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for brokers.
 */
@Service
@NoArgsConstructor
public class BrokerService extends BaseEntityService<Broker, BrokerDesc> {
}
