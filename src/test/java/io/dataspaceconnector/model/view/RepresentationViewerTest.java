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
//package io.dataspaceconnector.model.view;
//
//import io.dataspaceconnector.model.Representation;
//import io.dataspaceconnector.model.RepresentationDesc;
//import io.dataspaceconnector.model.RepresentationFactory;
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
