package de.fraunhofer.isst.dataspaceconnector.services.ids.builder;

import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Converts DSC rule to ids duty.
 */
@Component
public class IdsDutyBuilder extends IdsRuleBuilder<Duty> {
    IdsDutyBuilder(@NonNull final DeserializationService deserializer) {
        super(deserializer, Duty.class);
    }
}
