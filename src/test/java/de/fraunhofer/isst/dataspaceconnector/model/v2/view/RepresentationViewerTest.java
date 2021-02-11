package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RepresentationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RepresentationViewerTest {

    private RepresentationViewer factory;

    @Before
    public void init() {
        factory = new RepresentationViewer();
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
        final var representation = getRepresentation();

        final var view = factory.create(representation);

        Assert.assertNotNull(view);
        Assert.assertEquals(view.getTitle(), representation.getTitle());
        Assert.assertEquals(view.getMediaType(), representation.getMediaType());
        Assert.assertEquals(view.getLanguage(), representation.getLanguage());
    }

    Representation getRepresentation() {
        final var representationFactory = new RepresentationFactory();

        final var desc = new RepresentationDesc();
        desc.setTitle("Some title");
        desc.setType("Some type");
        desc.setLanguage("Some language");

        return representationFactory.create(desc);
    }
}
