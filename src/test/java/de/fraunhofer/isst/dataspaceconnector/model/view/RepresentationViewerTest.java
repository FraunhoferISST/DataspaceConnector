//package de.fraunhofer.isst.dataspaceconnector.model.view;
//
//import de.fraunhofer.isst.dataspaceconnector.model.Representation;
//import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
//import de.fraunhofer.isst.dataspaceconnector.model.RepresentationFactory;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//public class RepresentationViewerTest {
//
//    private RepresentationViewFactory factory;
//
//    @Before
//    public void init() {
//        factory = new RepresentationViewFactory();
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
//        final var representation = getRepresentation();
//
//        final var view = factory.create(representation);
//
//        Assert.assertNotNull(view);
//        Assert.assertEquals(view.getTitle(), representation.getTitle());
//        Assert.assertEquals(view.getMediaType(), representation.getMediaType());
//        Assert.assertEquals(view.getLanguage(), representation.getLanguage());
//    }
//
//    Representation getRepresentation() {
//        final var representationFactory = new RepresentationFactory();
//
//        final var desc = new RepresentationDesc();
//        desc.setTitle("Some title");
//        desc.setType("Some type");
//        desc.setLanguage("Some language");
//
//        return representationFactory.create(desc);
//    }
//}
