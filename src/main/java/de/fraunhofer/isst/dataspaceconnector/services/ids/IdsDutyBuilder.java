package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.Duty;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Converts DSC Rule to Infomodel Duty rule.
 */
@Component
public class IdsDutyBuilder extends IdsRuleBuilder<Duty> {
    IdsDutyBuilder(@NonNull final DeserializationService deserializer) {
        super(deserializer, Duty.class);
    }
}
