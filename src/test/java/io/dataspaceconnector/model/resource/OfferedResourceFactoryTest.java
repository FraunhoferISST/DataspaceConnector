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
package io.dataspaceconnector.model.resource;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.model.contract.Contract;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OfferedResourceFactoryTest {

    private OfferedResourceFactory factory = new OfferedResourceFactory();

    @Test
    public void default_title_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ResourceFactory.DEFAULT_TITLE);
    }

    @Test
    public void default_description_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ResourceFactory.DEFAULT_DESCRIPTION);
    }

    @Test
    public void default_keywords_is_DSC() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(Collections.singletonList("DSC"), ResourceFactory.DEFAULT_KEYWORDS);
    }

    @Test
    public void default_publisher_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ResourceFactory.DEFAULT_PUBLISHER);
    }

    @Test
    public void default_language_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ResourceFactory.DEFAULT_LANGUAGE);
    }

    @Test
    public void default_license_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ResourceFactory.DEFAULT_LICENSE);
    }

    @Test
    public void default_sovereign_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ResourceFactory.DEFAULT_SOVEREIGN);
    }

    @Test
    public void default_endpointDocumentation_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create(""), ResourceFactory.DEFAULT_ENDPOINT_DOCS);
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
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    public void create_validDesc_representationsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(0, result.getRepresentations().size());
    }

    @Test
    public void create_validDesc_contractsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(0, result.getContracts().size());
    }

    @Test
    public void create_validDesc_keywordsDefault() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(1, result.getKeywords().size());
    }

    @Test
    public void create_validDesc_catalogsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(0, result.getCatalogs().size());
    }

    /**
     * title.
     */

    @Test
    public void create_nullTitle_defaultTitle() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setTitle(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_TITLE, result.getTitle());
    }

    @Test
    public void update_differentTitle_setTitle() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setTitle("Random Title");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getTitle(), resource.getTitle());
    }

    @Test
    public void update_differentTitle_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setTitle("Random Title");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameTitle_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * description.
     */

    @Test
    public void create_nullDescription_defaultDescription() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setDescription(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_DESCRIPTION, result.getDescription());
    }

    @Test
    public void update_differentDescription_setDescription() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setDescription("Random Description");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getDescription(), resource.getDescription());
    }

    @Test
    public void update_differentDescription_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setDescription("Random Description");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameDescription_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * keywords.
     */

    @Test
    public void create_nullKeywords_defaultKeywords() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setKeywords(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertTrue(ResourceFactory.DEFAULT_KEYWORDS.containsAll(result.getKeywords()));
        assertEquals(ResourceFactory.DEFAULT_KEYWORDS.size(), result.getKeywords().size());
    }

    @Test
    public void update_differentKeywords_setKeywords() {
        /* ARRANGE */
        final var keywords = new ArrayList<String>();
        keywords.add("Default");
        keywords.add("Key");

        final var desc = new OfferedResourceDesc();
        desc.setKeywords(keywords);

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getKeywords(), resource.getKeywords());
    }

    @Test
    public void update_differentKeywords_returnTrue() {
        /* ARRANGE */
        final var keywords = new ArrayList<String>();
        keywords.add("Default");
        keywords.add("Key");

        final var desc = new OfferedResourceDesc();
        desc.setKeywords(keywords);

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameKeywords_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * publisher.
     */

    @Test
    public void create_nullPublisher_defaultPublisher() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setPublisher(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_PUBLISHER, result.getPublisher());
    }

    @Test
    public void update_differentPublisher_setPublisher() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setPublisher(URI.create("RandomPublisher"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getPublisher(), resource.getPublisher());
    }

    @Test
    public void update_differentPublisher_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setPublisher(URI.create("RandomPublisher"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_samePublisher_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }


    /**
     * language.
     */

    @Test
    public void create_nullLanguage_defaultLanguage() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLanguage(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_DESCRIPTION, result.getLanguage());
    }

    @Test
    public void update_differentLanguage_setLanguage() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLanguage("Random Language");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getLanguage(), resource.getLanguage());
    }

    @Test
    public void update_differentLanguage_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLanguage("Random Language");

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameLanguage_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * license.
     */

    @Test
    public void create_nullLicense_defaultLicense() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLicense(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_LICENSE, result.getLicense());
    }

    @Test
    public void update_differentLicense_setLicense() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLicense(URI.create("RandomLicense"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getLicense(), resource.getLicense());
    }

    @Test
    public void update_differentLicense_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setLicense(URI.create("RandomLicense"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameLicense_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * sovereign.
     */

    @Test
    public void create_nullSovereign_defaultSovereign() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSovereign(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_SOVEREIGN, result.getSovereign());
    }

    @Test
    public void update_differentSovereign_setSovereign() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSovereign(URI.create("RandomSovereign"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getSovereign(), resource.getSovereign());
    }

    @Test
    public void update_differentSovereign_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSovereign(URI.create("RandomSovereign"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameSovereign_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * endpointDocumentation.
     */

    @Test
    public void create_nullEndpointDocumentation_defaultEndpointDocumentation() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setEndpointDocumentation(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ResourceFactory.DEFAULT_ENDPOINT_DOCS, result.getEndpointDocumentation());
    }

    @Test
    public void update_differentEndpointDocumentation_setEndpointDocumentation() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setEndpointDocumentation(URI.create("RandomEndpointDocumentation"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getEndpointDocumentation(), resource.getEndpointDocumentation());
    }

    @Test
    public void update_differentEndpointDocumentation_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setEndpointDocumentation(URI.create("RandomEndpointDocumentation"));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameEndpointDocumentation_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * additional.
     */

    @Test
    public void create_nullAdditional_defaultAdditional() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setAdditional(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(new HashMap<>(), result.getAdditional());
    }

    @Test
    public void update_differentAdditional_setAdditional() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var representation = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertEquals(desc.getAdditional(), representation.getAdditional());
    }

    @Test
    public void update_differentAdditional_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var representation = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(representation, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameAdditional_returnFalse() {
        /* ARRANGE */
        final var representation = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(representation, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * version
     */

    @Test
    public void create_version_is_1() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(1, result.getVersion());
    }

    @Test
    public void update_same_sameVersion() {
        /* ARRANGE */
        var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(1, resource.getVersion());
    }

    @Test
    public void update_different_incrementVersion() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setTitle("RANDOM TITLE");

        var resource = factory.create(desc);

        /* ACT */
        factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        assertEquals(2, resource.getVersion());
    }

    /**
     * samples
     */

    @Test
    public void create_validDesc_samplesDefault() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new OfferedResourceDesc());

        /* ASSERT */
        assertNotNull(result.getSamples());
        assertTrue(result.getSamples().isEmpty());
    }

    @Test
    public void create_invalidUri_throwInvalidEntityException() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSamples(List.of(URI.create("https://resources/")));

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT & ASSERT*/
        assertThrows(InvalidEntityException.class, () -> factory.update(resource, desc));
    }

    @Test
    public void create_invalidResourceId_throwInvalidEntityException() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSamples(List.of(URI.create("https://api/resources/d8a6f765-9b94-4a27-a18d-fbe81636a784")));
        factory.setDoesExist(x -> { throw new InvalidEntityException(""); });

        final var resource = getResource();

        /* ACT & ASSERT*/
        assertThrows(InvalidEntityException.class, () -> factory.update(resource, desc));
    }

    @Test
    public void update_differentSamples_setSamples() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSamples(List.of(URI.create("https://api/resources/d8a6f765-9b94-4a27-a18d-fbe81636a784")));

        final var resource = factory.create(new OfferedResourceDesc());
        ReflectionTestUtils.setField(resource, "id", UUID.randomUUID());
        factory.setDoesExist(x -> true);

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getSamples(), resource.getSamples());
    }

    @Test
    public void update_differentSamples_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setSamples(List.of(URI.create("https://api/resources/d8a6f765-9b94-4a27-a18d-fbe81636a784")));

        final var resource = factory.create(new OfferedResourceDesc());
        ReflectionTestUtils.setField(resource, "id", UUID.randomUUID());
        factory.setDoesExist(x -> true);

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_differentSamples_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());
        factory.setDoesExist(x -> true);

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * payment modality
     */

    @Test
    public void update_differentPaymentModality_setPaymentModality() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setPaymentMethod(PaymentMethod.FIXED_PRICE);

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(desc.getPaymentMethod(), resource.getPaymentModality());
    }

    @Test
    public void update_differentPaymentModality_returnTrue() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        desc.setPaymentMethod(PaymentMethod.FIXED_PRICE);

        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_differentPaymentModality_returnFalse() {
        /* ARRANGE */
        final var resource = factory.create(new OfferedResourceDesc());

        /* ACT */
        final var result = factory.update(resource, new OfferedResourceDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    /**
     * update inputs.
     */

    @Test
    public void update_nullOfferedResource_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(null,
                new OfferedResourceDesc()));
    }

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var contract = factory.create(new OfferedResourceDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(contract, null));
    }

    @SneakyThrows
    private OfferedResource getResource() {
        final var constructor = OfferedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var resource = constructor.newInstance();
        ReflectionTestUtils.setField(resource, "title", "Hello");
        ReflectionTestUtils.setField(resource, "representations", new ArrayList<Contract>());
        ReflectionTestUtils.setField(resource, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return resource;
    }
}
