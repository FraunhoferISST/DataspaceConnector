//package de.fraunhofer.isst.dataspaceconnector.model.view;
//
//import de.fraunhofer.isst.dataspaceconnector.model.Resource;
//import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
//import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceFactory;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.net.URI;
//import java.util.Arrays;
//
//public class ResourceViewerTest {
//
//    private ResourceViewFactory factory;
//
//    @Before
//    public void init() {
//        factory = new ResourceViewFactory();
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
//        final var resource = getResource();
//
//        final var view = factory.create(resource);
//
//        Assert.assertNotNull(view);
//        Assert.assertEquals(view.getTitle(), resource.getTitle());
//        Assert.assertEquals(view.getDescription(), resource.getDescription());
//        Assert.assertEquals(view.getPublisher(), resource.getPublisher());
//        Assert.assertEquals(view.getKeywords(), resource.getKeywords());
//        Assert.assertEquals(view.getLicence(), resource.getLicence());
//        Assert.assertEquals(view.getLanguage(), resource.getLanguage());
//        Assert.assertEquals(view.getVersion(), resource.getVersion());
//    }
//
//    Resource getResource() {
//        final var resourceFactory = new OfferedResourceFactory();
//
//        final var desc = new ResourceDesc();
//        desc.setTitle("Some title");
//        desc.setDescription("Some description");
//        desc.setPublisher(URI.create("someone"));
//        desc.setKeywords(Arrays.asList("K1", "K2"));
//        desc.setLicence(URI.create("something"));
//        desc.setLanguage("Some language");
//
//        return resourceFactory.create(desc);
//    }
//}
