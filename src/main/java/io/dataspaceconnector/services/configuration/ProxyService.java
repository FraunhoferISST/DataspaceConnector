package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.Proxy;
import io.dataspaceconnector.model.ProxyDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for proxies.
 */
@Service
@NoArgsConstructor
public class ProxyService extends BaseEntityService<Proxy, ProxyDesc> {
}
