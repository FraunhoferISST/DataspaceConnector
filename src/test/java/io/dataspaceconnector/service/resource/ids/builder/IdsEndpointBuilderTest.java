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
package io.dataspaceconnector.service.resource.ids.builder;

import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.iais.eis.AppEndpointImpl;
import de.fraunhofer.iais.eis.AppEndpointType;
import de.fraunhofer.iais.eis.GenericEndpointImpl;
import de.fraunhofer.iais.eis.Language;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.datasource.DataSource;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {IdsEndpointBuilder.class})
public class IdsEndpointBuilderTest {

    @MockBean
    private SelfLinkHelper selfLinkHelper;

    @Autowired
    private IdsEndpointBuilder builder;

    private final String endpointLocation = "https://location.com";

    private final URI endpointDocumentation = URI.create("https://documentation.com");

    @BeforeEach
    void init() {
        when(selfLinkHelper.getSelfLink(any(Entity.class))).thenReturn(URI.create("https://link"));
    }

    @Test
    void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> builder.create(null));
    }

    @Test
    void create_inputGenericEndpoint_returnGenericEndpoint() {
        /* ARRANGE */
        final var endpoint = getGenericEndpoint();

        /* ACT */
        final var result = builder.create(endpoint);

        /* ASSERT */
        assertTrue(result instanceof GenericEndpointImpl);
        assertEquals(result.getPath(), endpoint.getLocation());
        assertEquals(result.getEndpointDocumentation().get(0), endpoint.getDocs());
        assertEquals(result.getEndpointInformation().get(0).getValue(), endpoint.getInfo());

        final var auth = (BasicAuth) endpoint.getDataSource().getAuthentication();
        final var idsAuth = ((GenericEndpointImpl) result).getGenericEndpointAuthentication();
        assertEquals(idsAuth.getAuthUsername(), auth.getUsername());
        assertEquals(idsAuth.getAuthPassword(), auth.getPassword());
    }

    @Test
    void create_inputAppEndpoint_returnAppEndpoint() {
        /* ARRANGE */
        final var endpoint = getAppEndpoint();

        /* ACT */
        final var result = builder.create(endpoint);

        /* ASSERT */
        assertTrue(result instanceof AppEndpointImpl);
        assertEquals(result.getPath(), endpoint.getLocation());
        assertEquals(result.getEndpointDocumentation().get(0), endpoint.getDocs());
        assertEquals(result.getEndpointInformation().get(0).getValue(), endpoint.getInfo());

        final var appEndpoint = (AppEndpointImpl) result;
        assertEquals(AppEndpointType.valueOf(endpoint.getEndpointType()),
                appEndpoint.getAppEndpointType());
        assertEquals(BigInteger.valueOf(endpoint.getEndpointPort()),
                appEndpoint.getAppEndpointPort());
        assertEquals(endpoint.getMediaType(),
                appEndpoint.getAppEndpointMediaType().getFilenameExtension());
        assertEquals(endpoint.getProtocol(), appEndpoint.getAppEndpointProtocol());
        assertEquals(Language.valueOf(endpoint.getLanguage()), appEndpoint.getLanguage());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private GenericEndpoint getGenericEndpoint() {
        final var auth = new BasicAuth("", "");
        ReflectionTestUtils.setField(auth, "username", "username");
        ReflectionTestUtils.setField(auth, "password", "password");

        final var dataSource = new DataSource();
        ReflectionTestUtils.setField(dataSource, "authentication", auth);

        final var endpoint = new GenericEndpoint();
        ReflectionTestUtils.setField(endpoint, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(endpoint, "location", endpointLocation);
        ReflectionTestUtils.setField(endpoint, "docs", endpointDocumentation);
        ReflectionTestUtils.setField(endpoint, "info", "info");
        ReflectionTestUtils.setField(endpoint, "dataSource", dataSource);
        ReflectionTestUtils.setField(endpoint, "additional", new HashMap<>());

        return endpoint;
    }

    private AppEndpoint getAppEndpoint() {
        final var endpoint = new AppEndpoint();
        ReflectionTestUtils.setField(endpoint, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(endpoint, "location", endpointLocation);
        ReflectionTestUtils.setField(endpoint, "docs", endpointDocumentation);
        ReflectionTestUtils.setField(endpoint, "info", "info");
        ReflectionTestUtils.setField(endpoint, "additional", new HashMap<>());
        ReflectionTestUtils.setField(endpoint, "endpointType", "INPUT_ENDPOINT");
        ReflectionTestUtils.setField(endpoint, "endpointPort", 5555);
        ReflectionTestUtils.setField(endpoint, "mediaType", "json");
        ReflectionTestUtils.setField(endpoint, "protocol", "http");
        ReflectionTestUtils.setField(endpoint, "language", "EN");

        return endpoint;
    }

}
