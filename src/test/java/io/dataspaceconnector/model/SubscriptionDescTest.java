package io.dataspaceconnector.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class SubscriptionDescTest {

    @Test
    public void equals_verify() {
        EqualsVerifier.simple().forClass(SubscriptionDesc.class).verify();
    }

}
