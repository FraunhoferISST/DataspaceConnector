package de.fraunhofer.isst.dataspaceconnector.utils;

import java.util.ArrayList;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.ProhibitionBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.Util;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
