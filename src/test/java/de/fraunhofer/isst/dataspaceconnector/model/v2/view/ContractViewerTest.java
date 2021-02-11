package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ContractViewerTest {

    private ContractViewer factory;

    @Before
    public void init() {
        factory = new ContractViewer();
    }

    @Test(expected = NullPointerException.class)
    public void create_null_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT*/
        factory.create(null);
    }

    @Test
    public void create_validDesc_validView() {
        final var contract = getContract();

        final var view = factory.create(contract);

        Assert.assertNotNull(view);
        Assert.assertEquals(view.getTitle(), contract.getTitle());
    }

    Contract getContract() {
        final var contractFactory = new ContractFactory();

        final var desc = new ContractDesc();
        desc.setTitle("Some Title");

        return contractFactory.create(desc);
    }
}
