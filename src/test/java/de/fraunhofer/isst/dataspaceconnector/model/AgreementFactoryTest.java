package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgreementFactoryTest {

    private AgreementFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new AgreementFactory();
    }

    @Test
    public void default_value_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", AgreementFactory.DEFAULT_VALUE);
    }

    @Test
    public void default_remoteId_is_genesis() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create("genesis"), AgreementFactory.DEFAULT_REMOTE_ID);
    }

    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    public void create_validDesc_creationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new AgreementDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new AgreementDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new AgreementDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    public void create_validDesc_artifactsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new AgreementDesc());

        /* ASSERT */
        assertEquals(0, result.getArtifacts().size());
    }

    /**
     * remoteId.
     */

    @Test
    public void create_nullRemoteId_defaultRemoteId() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setRemoteId(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(AgreementFactory.DEFAULT_REMOTE_ID, result.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_setRemoteId() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setRemoteId(URI.create("uri"));

        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        factory.update(agreement, desc);

        /* ASSERT */
        assertEquals(desc.getRemoteId(), agreement.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_returnTrue() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setRemoteId(URI.create("uri"));

        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        final var result = factory.update(agreement, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameRemoteId_returnFalse() {
        /* ARRANGE */
        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        final var result = factory.update(agreement, new AgreementDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * isConfirmed.
     */

    @Test
    public void update_differentConfirmed_setConfirmed() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setConfirmed(true);

        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        factory.update(agreement, desc);

        /* ASSERT */
        assertEquals(desc.isConfirmed(), agreement.isConfirmed());
    }

    @Test
    public void update_differentConfirmed_returnTrue() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setConfirmed(true);

        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        final var result = factory.update(agreement, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameConfirmed_returnFalse() {
        /* ARRANGE */
        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        final var result = factory.update(agreement, new AgreementDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * Value.
     */

    @Test
    public void create_nullValue_defaultValue() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setValue(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(AgreementFactory.DEFAULT_VALUE, result.getValue());
    }

    @Test
    public void update_differentValue_setValue() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setValue("Some Value");

        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        factory.update(agreement, desc);

        /* ASSERT */
        assertEquals(desc.getValue(), agreement.getValue());
    }

    @Test
    public void update_differentValue_returnTrue() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setValue("Some Value");

        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        final var result = factory.update(agreement, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameValue_returnFalse() {
        /* ARRANGE */
        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        final var result = factory.update(agreement, new AgreementDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * additional.
     */

    @Test
    public void create_nullAdditional_defaultAdditional() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setAdditional(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(new HashMap<>(), result.getAdditional());
    }

    @Test
    public void update_differentAdditional_setAdditional() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        factory.update(agreement, desc);

        /* ASSERT */
        assertEquals(desc.getAdditional(), agreement.getAdditional());
    }

    @Test
    public void update_differentAdditional_returnTrue() {
        /* ARRANGE */
        final var desc = new AgreementDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        final var result = factory.update(agreement, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameAdditional_returnFalse() {
        /* ARRANGE */
        final var agreement = factory.create(new AgreementDesc());

        /* ACT */
        final var result = factory.update(agreement, new AgreementDesc());

        /* ASSERT */
        assertFalse(result);
    }


    /**
     * update inputs.
     */

    @Test
    public void update_nullAgreement_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(null, new AgreementDesc()));
    }

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var agreement = factory.create(new AgreementDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(agreement, null));
    }
}
