package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.Duty;
import lombok.NonNull;

class IdsDutyBuilder extends IdsRuleBuilder<Duty> {
    IdsDutyBuilder(@NonNull final DeserializationService deserializer) {
        super(deserializer, Duty.class);
    }
}
