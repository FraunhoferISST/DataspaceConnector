package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContractFactoryTest {

    private ContractFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new ContractFactory();
    }

    @Test
    public void default_title_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ContractFactory.DEFAULT_TITLE);
    }

    @Test
    public void default_remoteId_is_genesis() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create("genesis"), ContractFactory.DEFAULT_REMOTE_ID);
    }

    @Test
    public void default_consumer_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ContractFactory.DEFAULT_CONSUMER);
    }

    @Test
    public void default_provider_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ContractFactory.DEFAULT_PROVIDER);
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
        final var result = factory.create(new ContractDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ContractDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ContractDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    public void create_validDesc_rulesEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ContractDesc());

        /* ASSERT */
        assertEquals(0, result.getRules().size());
    }

    @Test
    public void create_validDesc_resourcesEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ContractDesc());

        /* ASSERT */
        assertEquals(0, result.getResources().size());
    }

    /**
     * remoteId.
     */
    @Test
    public void create_nullRemoteId_defaultRemoteId() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setRemoteId(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ContractFactory.DEFAULT_REMOTE_ID, result.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_setRemoteId() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));

        final var contract = factory.create(desc);

        desc.setRemoteId(URI.create("uri"));

        /* ACT */
        factory.update(contract, desc);

        /* ASSERT */
        assertEquals(desc.getRemoteId(), contract.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_returnTrue() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setRemoteId(URI.create("uri"));

        final var contract = factory.create(desc);

        desc.setRemoteId(URI.create("differentUri"));

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameRemoteId_returnFalse() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * consumer.
     */

    @Test
    public void create_nullConsumer_defaultConsumer() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setConsumer(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ContractFactory.DEFAULT_CONSUMER, result.getConsumer());
    }

    @Test
    public void update_differentConsumer_setConsumer() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);
        desc.setConsumer(URI.create("uri"));

        /* ACT */
        factory.update(contract, desc);

        /* ASSERT */
        assertEquals(desc.getConsumer(), contract.getConsumer());
    }

    @Test
    public void update_differentConsumer_returnTrue() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);
        desc.setConsumer(URI.create("uri"));

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameConsumer_returnFalse() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setConsumer(URI.create("consumer"));
        final var contract = factory.create(desc);

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertFalse(result);
    }
    
    /**
     * provider.
     */
    
    @Test
    public void create_nullProvider_defaultProvider() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setProvider(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ContractFactory.DEFAULT_PROVIDER, result.getProvider());
    }

    @Test
    public void update_differentProvider_setProvider() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);
        desc.setProvider(URI.create("uri"));


        /* ACT */
        factory.update(contract, desc);

        /* ASSERT */
        assertEquals(desc.getProvider(), contract.getProvider());
    }

    @Test
    public void update_differentProvider_returnTrue() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);
        desc.setProvider(URI.create("uri"));

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameProvider_returnFalse() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setProvider(URI.create("randomProvider"));
        final var contract = factory.create(desc);

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * title.
     */

    @Test
    public void create_nullTitle_defaultTitle() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setTitle(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ContractFactory.DEFAULT_TITLE, result.getTitle());
    }

    @Test
    public void update_differentTitle_setTitle() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);
        desc.setTitle("Random Title");

        /* ACT */
        factory.update(contract, desc);

        /* ASSERT */
        assertEquals(desc.getTitle(), contract.getTitle());
    }

    @Test
    public void update_differentTitle_returnTrue() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);
        desc.setTitle("Random Title");

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameTitle_returnFalse() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setTitle("Random Title");
        final var contract = factory.create(desc);

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * start.
     */

//    @Test
//    public void create_nullStart_defaultStart() {
//        /* ARRANGE */
//        final var desc = new ContractDesc();
//        desc.setStart(null);
//
//        final var now = ZonedDateTime.now(ZoneOffset.UTC);
//
//        /* ACT */
//        final var result = factory.create(desc);
//
//        /* ASSERT */
//        assertTrue(now.before(result.getStart()));
//    }
//
//    @Test
//    public void update_differentStart_setStart() {
//        /* ARRANGE */
//        final var desc = new ContractDesc();
//        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
//
//        final var contract = factory.create(new ContractDesc());
//
//        /* ACT */
//        factory.update(contract, desc);
//
//        /* ASSERT */
//        assertEquals(desc.getStart(), contract.getStart());
//    }
//
//    @Test
//    public void update_differentStart_returnTrue() throws ParseException {
//        /* ARRANGE */
//        final var initialStartTime = ZonedDateTime.now(ZoneOffset.UTC);
//        final var initialEndTime = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse("20-Feb-2021 10:10:10");
//
//        final var initDesc = new ContractDesc();
//        initDesc.setStart(initialStartTime);
//        initDesc.setEnd(initialEndTime);
//
//        final var contract = factory.create(initDesc);
//
//        final var desc = new ContractDesc();
//        desc.setStart(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse("15-Feb-2021 10:10:10"));
//        desc.setEnd(initialEndTime);
//
//        /* ACT */
//        final var result = factory.update(contract, desc);
//
//        /* ASSERT */
//        assertTrue(result);
//    }
//
//    @Test
//    public void update_sameStart_returnFalse() {
//        /* ARRANGE */
//        final var contract = factory.create(new ContractDesc());
//
//        /* ACT */
//        final var result = factory.update(contract, new ContractDesc());
//
//        /* ASSERT */
//        assertFalse(result);
//    }

    /**
     * additional.
     */
    @Test
    public void create_nullAdditional_defaultAdditional() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setAdditional(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(new HashMap<>(), result.getAdditional());
    }

    @Test
    public void update_differentAdditional_setAdditional() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);
        desc.setAdditional(Map.of("Y", "X"));

        /* ACT */
        factory.update(contract, desc);

        /* ASSERT */
        assertEquals(desc.getAdditional(), contract.getAdditional());
    }

    @Test
    public void update_differentAdditional_returnTrue() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);
        desc.setAdditional(Map.of("Y", "X"));

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameAdditional_returnFalse() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setStart(ZonedDateTime.now(ZoneOffset.UTC));
        desc.setEnd(ZonedDateTime.now(ZoneOffset.UTC));
        final var contract = factory.create(desc);

        /* ACT */
        final var result = factory.update(contract, desc);

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * update inputs.
     */

    @Test
    public void update_nullContract_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(null, new ContractDesc()));
    }

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var contract = factory.create(new ContractDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(contract, null));
    }
}
