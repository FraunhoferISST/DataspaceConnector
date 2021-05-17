package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.ClearingHouse;
import io.dataspaceconnector.model.ClearingHouseDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for clearing houses.
 */
@Service
@NoArgsConstructor
public class ClearingHouseService extends BaseEntityService<ClearingHouse, ClearingHouseDesc> {
}
