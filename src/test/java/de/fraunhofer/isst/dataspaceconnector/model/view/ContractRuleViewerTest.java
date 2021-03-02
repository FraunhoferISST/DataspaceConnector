//package de.fraunhofer.isst.dataspaceconnector.model.view;
//
//import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
//import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
//import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleFactory;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//public class ContractRuleViewerTest {
//
//    private RuleViewFactory factory;
//
//    @Before
//    public void init() {
//        factory = new RuleViewFactory();
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void create_null_throwNullPointerException() {
//        /* ARRANGE */
//        // Nothing to arrange.
//
//        /* ACT && ASSERT*/
//        factory.create(null);
//    }
//
//    @Test
//    public void create_validDesc_validView() {
//        final var rule = getRule();
//
//        final var view = factory.create(rule);
//
//        Assert.assertNotNull(view);
//        Assert.assertEquals(view.getTitle(), rule.getTitle());
//        Assert.assertEquals(view.getValue(), rule.getValue());
//    }
//
//    ContractRule getRule() {
//        final var ruleFactory = new ContractRuleFactory();
//
//        final var desc = new ContractRuleDesc();
//        desc.setTitle("Some Title");
//        desc.setRule("Some Rule");
//
//        return ruleFactory.create(desc);
//    }
//}
