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
package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.Proxy;
import io.dataspaceconnector.model.ProxyDesc;
import io.dataspaceconnector.model.ProxyFactory;
import io.dataspaceconnector.repositories.ProxyRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {ProxyService.class})
public class ProxyServiceTest {


    @MockBean
    private ProxyRepository proxyRepository;

    @MockBean
    private ProxyFactory proxyFactory;

    @Autowired
    @InjectMocks
    private ProxyService proxyService;

    Proxy proxy = getProxy();
    ProxyDesc proxyDesc = getProxyDesc();

    UUID validId = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");

    /**********************************************************************
     * SETUP
     **********************************************************************/
    @BeforeEach
    public void init() {
        Mockito.when(proxyFactory.create(any())).thenReturn(proxy);
        Mockito.when(proxyRepository.saveAndFlush(Mockito.eq(proxy)))
                .thenReturn(proxy);
        Mockito.when(proxyRepository.findById(Mockito.eq(proxy.getId())))
                .thenReturn(Optional.of(proxy));
    }

    /**********************************************************************
     * GET
     **********************************************************************/
    @Test
    public void get_nullId_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> proxyService.get(null));
    }

    @Test
    public void get_knownId_returnProxy() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = proxyService.get(proxy.getId());

        /* ASSERT */
        assertEquals(proxy.getId(), result.getId());
        assertEquals(proxy.getProxyURI(), result.getProxyURI());
    }

    /**********************************************************************
     * CREATE
     **********************************************************************/
    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> proxyService.create(null));
    }

    @Test
    public void create_ValidDesc_returnHasId() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var newProxy = proxyService.create(proxyDesc);

        /* ASSERT */
        assertEquals(proxy, newProxy);
    }

    /**********************************************************************
     * UPDATE
     **********************************************************************/
    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> proxyService.update(proxy.getId(), null));
    }

    @Test
    public void update_NewDesc_returnUpdatedEntity() {
        /* ARRANGE */
        final var shouldLookLike = getProxyFromValidDesc(validId,
                getNewProxy(getUpdatedProxyDesc()));

        /* ACT */
        final var after =
                proxyService.update(validId, getUpdatedProxyDesc());

        /* ASSERT */
        assertEquals(after, shouldLookLike);
    }

    /**********************************************************************
     * UTILITIES
     **********************************************************************/
    @SneakyThrows
    private Proxy getProxy() {
        final var desc = getProxyDesc();

        final var proxyConstructor = Proxy.class.getConstructor();

        final var proxy = proxyConstructor.newInstance();

        final var idField = proxy.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(proxy, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        final var proxyUriField = proxy.getClass().getDeclaredField("proxyURI");
        proxyUriField.setAccessible(true);
        proxyUriField.set(proxy, desc.getProxyURI());

        return proxy;
    }

    private Proxy getNewProxy(final ProxyDesc updatedProxyDesc) {
        return proxyFactory.create(updatedProxyDesc);
    }

    @SneakyThrows
    private Proxy getProxyFromValidDesc(final UUID id,
                                                final Proxy proxy) {
        final var idField = proxy.getClass().getSuperclass()
                .getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(proxy, id);

        return proxy;
    }

    private ProxyDesc getProxyDesc() {
        final var desc = new ProxyDesc();
        desc.setProxyURI(URI.create("https://example"));

        return desc;
    }

    private ProxyDesc getUpdatedProxyDesc() {
        final var desc = new ProxyDesc();
        desc.setProxyURI(URI.create("https://newexample"));

        return desc;
    }
}
