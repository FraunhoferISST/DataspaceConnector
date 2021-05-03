package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractFactory;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleFactory;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsContractBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsDutyBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsPermissionBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsProhibitionBuilder;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ContractFactory.class, ContractRuleFactory.class,
        IdsContractBuilder.class, IdsPermissionBuilder.class, IdsProhibitionBuilder.class,
        IdsDutyBuilder.class, DeserializationService.class, SerializerProvider.class})
public class IdsContractBuilderTest {

    @Autowired
    private ContractFactory contractFactory;

    @Autowired
    private ContractRuleFactory ruleFactory;

    @Autowired
    private IdsContractBuilder idsContractBuilder;

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    private final URI provider = URI.create("https://provider.com");

    private final URI consumer = URI.create("https://consumer.com");

    @Test
    public void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> idsContractBuilder.create(null));
    }

    @Test
    public void create_defaultDepth_returnCompleteContract() {
        /* ARRANGE */
        final var contract = getContract();

        /* ACT */
        final var idsContract = idsContractBuilder.create(contract);

        /* ASSERT */
        assertTrue(idsContract.getId().isAbsolute());
        assertTrue(idsContract.getId().toString().contains(contract.getId().toString()));

        assertEquals(provider, idsContract.getProvider());
        assertEquals(consumer, idsContract.getConsumer());
        assertEquals(IdsUtils.getGregorianOf(date), idsContract.getContractStart());
        assertEquals(IdsUtils.getGregorianOf(date), idsContract.getContractEnd());
        assertNull(idsContract.getProperties());

        assertTrue(idsContract.getProhibition().isEmpty());
        assertTrue(idsContract.getObligation().isEmpty());
        assertEquals(1, idsContract.getPermission().size());
        assertEquals(Action.USE, idsContract.getPermission().get(0).getAction().get(0));
        assertNull(idsContract.getPermission().get(0).getConstraint());
    }

    @Test
    public void create_defaultDepthWithAdditional_returnCompleteContract() {
        /* ARRANGE */
        final var contract = getContractWithAdditional();

        /* ACT */
        final var idsContract = idsContractBuilder.create(contract);

        /* ASSERT */
        assertTrue(idsContract.getId().isAbsolute());
        assertTrue(idsContract.getId().toString().contains(contract.getId().toString()));

        assertEquals(provider, idsContract.getProvider());
        assertEquals(consumer, idsContract.getConsumer());
        assertEquals(IdsUtils.getGregorianOf(date), idsContract.getContractStart());
        assertEquals(IdsUtils.getGregorianOf(date), idsContract.getContractEnd());

        assertNotNull(idsContract.getProperties());
        assertEquals(1, idsContract.getProperties().size());
        assertEquals("value", idsContract.getProperties().get("key"));

        assertTrue(idsContract.getProhibition().isEmpty());
        assertTrue(idsContract.getObligation().isEmpty());
        assertEquals(1, idsContract.getPermission().size());
        assertEquals(Action.USE, idsContract.getPermission().get(0).getAction().get(0));
        assertNull(idsContract.getPermission().get(0).getConstraint());
    }

    @Test
    public void create_maxDepth0_returnContractWithoutRules() {
        /* ARRANGE */
        final var contract = getContract();

        /* ACT */
        final var idsContract = idsContractBuilder.create(contract, 0);

        /* ASSERT */
        assertTrue(idsContract.getId().isAbsolute());
        assertTrue(idsContract.getId().toString().contains(contract.getId().toString()));

        assertEquals(provider, idsContract.getProvider());
        assertEquals(consumer, idsContract.getConsumer());
        assertEquals(IdsUtils.getGregorianOf(date), idsContract.getContractStart());
        assertEquals(IdsUtils.getGregorianOf(date), idsContract.getContractEnd());
        assertNull(idsContract.getProperties());

        assertNull(idsContract.getPermission());
        assertNull(idsContract.getProhibition());
        assertNull(idsContract.getObligation());
    }

    @Test
    public void create_maxDepth5_returnCompleteContract() {
        /* ARRANGE */
        final var contract = getContract();

        /* ACT */
        final var idsContract = idsContractBuilder.create(contract, 5);

        /* ASSERT */
        assertTrue(idsContract.getId().isAbsolute());
        assertTrue(idsContract.getId().toString().contains(contract.getId().toString()));

        assertEquals(provider, idsContract.getProvider());
        assertEquals(consumer, idsContract.getConsumer());
        assertEquals(IdsUtils.getGregorianOf(date), idsContract.getContractStart());
        assertEquals(IdsUtils.getGregorianOf(date), idsContract.getContractEnd());
        assertNull(idsContract.getProperties());

        assertTrue(idsContract.getProhibition().isEmpty());
        assertTrue(idsContract.getObligation().isEmpty());
        assertEquals(1, idsContract.getPermission().size());
        assertEquals(Action.USE, idsContract.getPermission().get(0).getAction().get(0));
        assertNull(idsContract.getPermission().get(0).getConstraint());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    @SneakyThrows
    private ContractRule getRule() {
        final var value = "{\n"
                + "    \"@type\" : \"ids:Permission\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/permission/ae138d4f-f01d-4358"
                + "-89a7-73e7c560f3de\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"provide-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:USE\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";

        final var ruleDesc = new ContractRuleDesc();
        ruleDesc.setTitle("title");
        ruleDesc.setValue(value);
        final var rule = ruleFactory.create(ruleDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(rule, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(rule, date);

        return rule;
    }

    @SneakyThrows
    private Contract getContract() {
        final var contractDesc = new ContractDesc();
        contractDesc.setTitle("title");
        contractDesc.setStart(date);
        contractDesc.setEnd(date);
        contractDesc.setProvider(provider);
        contractDesc.setConsumer(consumer);

        final var contract = contractFactory.create(contractDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(contract, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(contract, date);

        final var modificationDateField = AbstractEntity.class.getDeclaredField("modificationDate");
        modificationDateField.setAccessible(true);
        modificationDateField.set(contract, date);

        final var rulesField = Contract.class.getDeclaredField("rules");
        rulesField.setAccessible(true);
        rulesField.set(contract, Collections.singletonList(getRule()));

        return contract;
    }

    @SneakyThrows
    private Contract getContractWithAdditional() {
        final var contract = getContract();
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        final var additionalField = AbstractEntity.class.getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(contract, additional);

        return contract;
    }

}
