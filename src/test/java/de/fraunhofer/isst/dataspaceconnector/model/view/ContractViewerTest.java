//package de.fraunhofer.isst.dataspaceconnector.model.view;
//
//import de.fraunhofer.isst.dataspaceconnector.model.Contract;
//import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
//import de.fraunhofer.isst.dataspaceconnector.model.ContractFactory;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//public class ContractViewerTest {
//
//    private ContractViewFactory factory;
//
//    @Before
//    public void init() {
//        factory = new ContractViewFactory();
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
//        final var contract = getContract();
//
//        final var view = factory.create(contract);
//
//        Assert.assertNotNull(view);
//        Assert.assertEquals(view.getTitle(), contract.getTitle());
//    }
//
//    Contract getContract() {
//        final var contractFactory = new ContractFactory();
//
//        final var desc = new ContractDesc();
//        desc.setTitle("Some Title");
//
//        return contractFactory.create(desc);
//    }
//}
