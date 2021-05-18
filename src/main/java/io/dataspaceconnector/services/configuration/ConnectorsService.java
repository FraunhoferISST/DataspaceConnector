package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.Connector;
import io.dataspaceconnector.model.ConnectorDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for connector.
 */
@Service
@NoArgsConstructor
public class ConnectorsService extends BaseEntityService<Connector, ConnectorDesc> {
}
