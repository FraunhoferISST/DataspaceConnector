package io.dataspaceconnector.model;

import java.util.UUID;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class SubscriberTest {

    @Test
    public void equals_verify() {
        EqualsVerifier.simple()
                .forClass(Subscriber.class)
                .withPrefabValues(RequestedResource.class, getRequestedResource1(), getRequestedResource2())
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    private RequestedResource getRequestedResource1() {
        final var resource = new RequestedResource();
        resource.setId(UUID.randomUUID());
        resource.setTitle("resource 1");
        return resource;
    }

    private RequestedResource getRequestedResource2() {
        final var resource = new RequestedResource();
        resource.setId(UUID.randomUUID());
        resource.setTitle("resource 2");
        return resource;
    }

}
