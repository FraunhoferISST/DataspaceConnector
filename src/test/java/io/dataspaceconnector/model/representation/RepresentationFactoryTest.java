/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.model.representation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepresentationFactoryTest {

    private RepresentationFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new RepresentationFactory();
    }

    @Test
    public void default_title_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", RepresentationFactory.DEFAULT_TITLE);
    }

    @Test
    public void default_remoteId_is_genesis() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create("genesis"), RepresentationFactory.DEFAULT_REMOTE_ID);
    }

    @Test
    public void default_language_is_EN() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("EN", RepresentationFactory.DEFAULT_LANGUAGE);
    }

    @Test
    public void default_mediaType_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", RepresentationFactory.DEFAULT_MEDIA_TYPE);
    }

    @Test
    public void default_standard_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", RepresentationFactory.DEFAULT_STANDARD);
    }

    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    public void create_validDesc_creationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new RepresentationDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new RepresentationDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new RepresentationDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    public void create_validDesc_artifactsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new RepresentationDesc());

        /* ASSERT */
        assertEquals(0, result.getArtifacts().size());
    }

    @Test
    public void create_validDesc_resourcesEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new RepresentationDesc());

        /* ASSERT */
        assertEquals(0, result.getResources().size());
    }

    /**
     * remoteId.
     */

    @Test
    public void create_nullRemoteId_defaultRemoteId() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setRemoteId(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(RepresentationFactory.DEFAULT_REMOTE_ID, result.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_setRemoteId() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setRemoteId(URI.create("uri"));

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertEquals(desc.getRemoteId(), representation.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_returnTrue() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setRemoteId(URI.create("uri"));

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameRemoteId_returnFalse() {
        /* ARRANGE */
        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, new RepresentationDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * title.
     */

    @Test
    public void create_nullTitle_defaultTitle() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setTitle(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(RepresentationFactory.DEFAULT_TITLE, result.getTitle());
    }

    @Test
    public void update_differentTitle_setTitle() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setTitle("Random Title");

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertEquals(desc.getTitle(), representation.getTitle());
    }

    @Test
    public void update_differentTitle_returnTrue() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setTitle("Random Title");

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameTitle_returnFalse() {
        /* ARRANGE */
        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, new RepresentationDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * mediaType.
     */

    @Test
    public void create_nullMediaType_defaultMediaType() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setMediaType(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(RepresentationFactory.DEFAULT_MEDIA_TYPE, result.getMediaType());
    }

    @Test
    public void update_differentMediaType_setMediaType() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setMediaType("Random MediaType");

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertEquals(desc.getMediaType(), representation.getMediaType());
    }

    @Test
    public void update_differentMediaType_returnTrue() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setMediaType("Random MediaType");

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameMediaType_returnFalse() {
        /* ARRANGE */
        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, new RepresentationDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * language.
     */

    @Test
    public void create_nullLanguage_defaultLanguage() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setLanguage(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(RepresentationFactory.DEFAULT_LANGUAGE, result.getLanguage());
    }

    @Test
    public void update_differentLanguage_setLanguage() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setLanguage("Random Language");

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertEquals(desc.getLanguage(), representation.getLanguage());
    }

    @Test
    public void update_differentLanguage_returnTrue() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setLanguage("Random Language");

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameLanguage_returnFalse() {
        /* ARRANGE */
        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, new RepresentationDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * standard.
     */

    @Test
    public void create_nullStandard_defaultStandard() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setStandard(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(RepresentationFactory.DEFAULT_STANDARD, result.getStandard());
    }

    @Test
    public void update_differentStandard_setStandard() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setStandard("Random Standard");

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertEquals(desc.getStandard(), representation.getStandard());
    }

    @Test
    public void update_differentStandard_returnTrue() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setStandard("Random Standard");

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameStandard_returnFalse() {
        /* ARRANGE */
        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, new RepresentationDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * additional.
     */

    @Test
    public void create_nullAdditional_defaultAdditional() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setAdditional(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(new HashMap<>(), result.getAdditional());
    }

    @Test
    public void update_differentAdditional_setAdditional() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertEquals(desc.getAdditional(), representation.getAdditional());
    }

    @Test
    public void update_differentAdditional_returnTrue() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameAdditional_returnFalse() {
        /* ARRANGE */
        final var representation = factory.create(new RepresentationDesc());

        /* ACT */
        final var result = factory.update(representation, new RepresentationDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * update inputs.
     */

    @Test
    public void update_nullRepresentation_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(null,
                new RepresentationDesc()));
    }

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var contract = factory.create(new RepresentationDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(contract, null));
    }
}
