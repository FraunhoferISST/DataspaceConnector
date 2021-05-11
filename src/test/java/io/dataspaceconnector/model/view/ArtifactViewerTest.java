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
//import io.dataspaceconnector.model.Artifact;
//import io.dataspaceconnector.model.ArtifactDesc;
//import io.dataspaceconnector.model.ArtifactFactory;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Random;
//
//public class ArtifactViewerTest {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactViewerTest.class);
//
//    private ArtifactViewFactory factory;
//
//    @Before
//    public void init() {
//        factory = new ArtifactViewFactory();
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
//        final var artifact = getLocalArtifact();
//
//        final var rndValue = new Random(123456).nextInt(10);
//        LOGGER.info("Testing with {} artifact increments.", rndValue);
//        for(int i = 0; i < rndValue; i++)
//            artifact.incrementAccessCounter();
//
//        final var view = factory.create(artifact);
//
//        Assert.assertNotNull(view);
//        Assert.assertEquals(view.getTitle(), artifact.getTitle());
//        Assert.assertEquals(view.getNumAccessed(), artifact.getNumAccessed());
//    }
//
//
//    Artifact getLocalArtifact() {
//        final var artifactFactory = new ArtifactFactory();
//
//        final var desc = new ArtifactDesc();
//        desc.setTitle("Some Title");
//        desc.setValue("Value");
//
//        return artifactFactory.create(desc);
//    }
//}
