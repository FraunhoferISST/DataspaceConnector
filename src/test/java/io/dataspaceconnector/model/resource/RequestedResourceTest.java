package io.dataspaceconnector.model.resource;

import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.representation.Representation;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RequestedResourceTest {

    @Test
    public void equalsAndHash_will_pass() {
        final var c1 = new Catalog();
        final var c2 = new Catalog();
         ReflectionTestUtils.setField(c2, "title", "haha");

         final var r1 = new Representation();
         final var r2 = new Representation();
         ReflectionTestUtils.setField(r2, "title", "haha");

         final var co1 = new Contract();
         final var co2 = new Contract();
         ReflectionTestUtils.setField(co2, "title", "haha");

        EqualsVerifier.simple().forClass(RequestedResource.class)
                      .withPrefabValues(Catalog.class, c1, c2)
                      .withPrefabValues(Representation.class, r1, r2)
                      .withPrefabValues(Contract.class, co1, co2)
                      .withIgnoredFields("id")
                      .verify();
    }
}
