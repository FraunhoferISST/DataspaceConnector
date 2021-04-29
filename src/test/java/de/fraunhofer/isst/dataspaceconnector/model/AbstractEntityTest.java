package de.fraunhofer.isst.dataspaceconnector.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class AbstractEntityTest {

    @Test
    public void verify_equals() {
        EqualsVerifier.forClass(AbstractEntity.class).verify();
    }

}
