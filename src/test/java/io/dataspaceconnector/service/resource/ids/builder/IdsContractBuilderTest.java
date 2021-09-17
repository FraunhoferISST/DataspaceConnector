/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.ids.messaging.util.SerializerProvider;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.contract.ContractDesc;
import io.dataspaceconnector.model.contract.ContractFactory;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.rule.ContractRuleDesc;
import io.dataspaceconnector.model.rule.ContractRuleFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @MockBean
    private SelfLinkHelper selfLinkHelper;

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    private final URI provider = URI.create("https://provider.com");

    private final URI consumer = URI.create("https://consumer.com");

    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    void init() {
        final var uri = URI.create("https://" + uuid);
        when(selfLinkHelper.getSelfLink(any(Entity.class))).thenReturn(uri);
    }

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
        assertEquals(ToIdsObjectMapper.getGregorianOf(date), idsContract.getContractStart());
        assertEquals(ToIdsObjectMapper.getGregorianOf(date), idsContract.getContractEnd());
        assertNull(idsContract.getProperties());

        assertTrue(idsContract.getProhibition().isEmpty());
        assertTrue(idsContract.getObligation().isEmpty());
        assertEquals(1, idsContract.getPermission().size());
        assertEquals(Action.USE, idsContract.getPermission().get(0).getAction().get(0));
        assertTrue(idsContract.getPermission().get(0).getConstraint().isEmpty());
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
        assertEquals(ToIdsObjectMapper.getGregorianOf(date), idsContract.getContractStart());
        assertEquals(ToIdsObjectMapper.getGregorianOf(date), idsContract.getContractEnd());

        assertNotNull(idsContract.getProperties());
        assertEquals(1, idsContract.getProperties().size());
        assertEquals("value", idsContract.getProperties().get("key"));

        assertTrue(idsContract.getProhibition().isEmpty());
        assertTrue(idsContract.getObligation().isEmpty());
        assertEquals(1, idsContract.getPermission().size());
        assertEquals(Action.USE, idsContract.getPermission().get(0).getAction().get(0));
        assertTrue(idsContract.getPermission().get(0).getConstraint().isEmpty());
    }

    @Test
    public void create_maxDepth0_returnNull() {
        /* ARRANGE */
        final var contract = getContract();

        /* ACT */
        final var idsContract = idsContractBuilder.create(contract, 0);

        /* ASSERT */
        assertNull(idsContract);
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
        assertEquals(ToIdsObjectMapper.getGregorianOf(date), idsContract.getContractStart());
        assertEquals(ToIdsObjectMapper.getGregorianOf(date), idsContract.getContractEnd());
        assertNull(idsContract.getProperties());

        assertTrue(idsContract.getProhibition().isEmpty());
        assertTrue(idsContract.getObligation().isEmpty());
        assertEquals(1, idsContract.getPermission().size());
        assertEquals(Action.USE, idsContract.getPermission().get(0).getAction().get(0));
        assertTrue(idsContract.getPermission().get(0).getConstraint().isEmpty());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    @SneakyThrows
    private ContractRule getRule() {
        final var value = "{\n"
                + "   \"@context\" : {\n"
                + "      \"ids\" : \"https://w3id.org/idsa/core/\",\n"
                + "      \"idsc\" : \"https://w3id.org/idsa/code/\"\n"
                + "      },"
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

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(rule, uuid);

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
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

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(contract, uuid);

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(contract, date);

        final var modificationDateField = Entity.class.getDeclaredField("modificationDate");
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

        final var additionalField = Entity.class.getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(contract, additional);

        return contract;
    }

}
