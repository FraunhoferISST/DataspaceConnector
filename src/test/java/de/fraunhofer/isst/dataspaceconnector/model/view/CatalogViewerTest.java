//package de.fraunhofer.isst.dataspaceconnector.model.view;
//
//import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
//import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
//import de.fraunhofer.isst.dataspaceconnector.model.CatalogFactory;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//public class CatalogViewerTest {
//
//    private CatalogViewFactory factory;
//
//    @Before
//    public void init() {
//        factory = new CatalogViewFactory();
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
//        final var catalog = getCatalog();
//
//        final var view = factory.create(catalog);
//
//        Assert.assertNotNull(view);
//        Assert.assertEquals(view.getTitle(), catalog.getTitle());
//        Assert.assertEquals(view.getDescription(), catalog.getDescription());
//    }
//
//
//    Catalog getCatalog() {
//        final var catalogFactory = new CatalogFactory();
//
//        final var desc = new CatalogDesc();
//        desc.setTitle("Some Title");
//        desc.setDescription("Value");
//
//        return catalogFactory.create(desc);
//    }
//}
