package de.fraunhofer.isst.dataspaceconnector.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.ProhibitionBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PolicyUtilsTest {

    @Test
    public void compareRules_null_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(PolicyUtils.compareRules(null, null));
    }

    @Test
    public void compareRules_leftNull_returnFalse() {
        /* ACT && ASSERT */
        assertFalse(PolicyUtils.compareRules(null, new ArrayList<>()));
    }

    @Test
    public void compareRules_rightNull_returnFalse() {
        /* ACT && ASSERT */
        assertFalse(PolicyUtils.compareRules(new ArrayList<>(), null));
    }

    @Test
    public void compareRules_sameList_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(PolicyUtils.compareRules(Util.asList(getRuleOne(), getRuleTwo()), Util.asList(getRuleOne(), getRuleTwo())));
    }

    @Test
    public void compareRules_sameSets_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(PolicyUtils.compareRules(Util.asList(getRuleOne(), getRuleTwo(), getRuleOne()), Util.asList(getRuleOne(), getRuleTwo())));
    }

    @Test
    public void compareRules_differentSets_returnFalse() {
        /* ACT && ASSERT */
        assertFalse(PolicyUtils.compareRules(Util.asList(getRuleOne(), getRuleTwo(), getRuleOne()), Util.asList(getRuleOne(), getRuleThree())));
    }

    /**
     * removeContractsWithInvalidConsumer
     */
    @Test
    public void removeContractsWithInvalidConsumer_sameConsumer_removeNothing() {
        /* ARRANGE */
        final var issuer = URI.create("https://someConsumer");
        final var list = List.of(getContractWithConsumer(), getContractWithoutConsumer());

        /* ACT */
        final var result = PolicyUtils.removeContractsWithInvalidConsumer(list, issuer);

        /* ASSERT */
        assertEquals(list, result);
    }

    @Test
    public void removeContractsWithInvalidConsumer_differentConsumer_removeRestrictedOffer() {
        /* ARRANGE */
        final var issuer = URI.create("https://someOtherConsumer");
        final var list = List.of(getContractWithConsumer(), getContractWithoutConsumer());

        /* ACT */
        final var result = PolicyUtils.removeContractsWithInvalidConsumer(list, issuer);

        /* ASSERT */
        assertEquals(List.of(getContractWithoutConsumer()), result);
    }

    @Test
    public void removeContractsWithInvalidConsumer_nullList_throwIllegalArgumentException() {
        /* ARRANGE */
        final var issuer = URI.create("https://someOtherConsumer");

        /* ACT && ASSERT*/
        assertThrows(IllegalArgumentException.class, () -> PolicyUtils.removeContractsWithInvalidConsumer(null, issuer));
    }

    @Test
    public void removeContractsWithInvalidConsumer_nullIssuer_throwIllegalArgumentException() {
        /* ARRANGE */
        final var list = List.of(getContractWithConsumer(), getContractWithoutConsumer());

        /* ACT && ASSERT*/
        assertThrows(IllegalArgumentException.class, () -> PolicyUtils.removeContractsWithInvalidConsumer(list, null));
    }

    @Test
    public void removeContractsWithInvalidConsumer_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT*/
        assertThrows(IllegalArgumentException.class, () -> PolicyUtils.removeContractsWithInvalidConsumer(null, null));
    }

    /**
     * Utilities
     */

    public Rule getRuleOne() {
        return new DutyBuilder()
                ._action_(Util.asList(getActionOne()))
                .build();
    }

    public Rule getRuleTwo() {
        return new ProhibitionBuilder()
                ._action_(Util.asList(getActionsTwo()))
                .build();
    }

    public Rule getRuleThree() {
        return new PermissionBuilder()
                ._action_(Util.asList(getActionThree()))
                .build();
    }

    public Action getActionOne() {
        return Action.USE;
    }

    public Action getActionsTwo() {
        return Action.NOTIFY;
    }

    public Action getActionThree() {
        return Action.LOG;
    }

    @SneakyThrows
    private Contract getContractWithoutConsumer() {
        final var constructor = Contract.class.getConstructor();
        constructor.setAccessible(true);

        final var contract = constructor.newInstance();

        final var titleField = contract.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(contract, "Catalog without consumer");

        final var issuerField = contract.getClass().getDeclaredField("consumer");
        issuerField.setAccessible(true);
        issuerField.set(contract, URI.create(""));

        return contract;
    }


    @SneakyThrows
    private Contract getContractWithConsumer() {
        final var constructor = Contract.class.getConstructor();
        constructor.setAccessible(true);

        final var contract = constructor.newInstance();

        final var titleField = contract.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(contract, "Catalog with consumer");

        final var issuerField = contract.getClass().getDeclaredField("consumer");
        issuerField.setAccessible(true);
        issuerField.set(contract, URI.create("https://someConsumer"));

        return contract;
    }
}
