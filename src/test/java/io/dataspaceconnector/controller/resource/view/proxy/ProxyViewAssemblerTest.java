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
package io.dataspaceconnector.controller.resource.view.proxy;

import java.net.URI;
import java.util.List;

import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.proxy.Proxy;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProxyViewAssemblerTest {

    @Test
    public void create_ValidProxyWithoutAuth_returnProxyView() {
        /* ARRANGE */
        final var shouldLookLike = getProxy_withoutAuth();

        /* ACT */
        final var after = getProxyView_withoutAuth();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
        assertFalse(after.getExclusions().isEmpty());
        assertEquals(after.getExclusions().get(0), shouldLookLike.getExclusions().get(0));
        assertFalse(after.isAuthenticationSet());
    }

    @Test
    public void create_ValidProxyWithAuth_returnProxyView() {
        /* ARRANGE */
        final var shouldLookLike = getProxy_withAuth();

        /* ACT */
        final var after = getProxyView_withAuth();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
        assertFalse(after.getExclusions().isEmpty());
        assertEquals(after.getExclusions().get(0), shouldLookLike.getExclusions().get(0));
        assertTrue(after.isAuthenticationSet());
    }

    private ProxyView getProxyView_withoutAuth() {
        final var proxyViewAssembler = new ProxyViewAssembler();
        return proxyViewAssembler.toModel(getProxy_withoutAuth());
    }

    private Proxy getProxy_withoutAuth() {
        final var proxyFactory = new ProxyFactory();
        return proxyFactory.create(getProxyDesc_withoutAuth());
    }

    private ProxyDesc getProxyDesc_withoutAuth() {
        final var desc = new ProxyDesc();
        desc.setLocation(URI.create("https://localhost"));
        desc.setExclusions(List.of("https://localhost:8081"));

        return desc;
    }

    private ProxyView getProxyView_withAuth() {
        final var proxyViewAssembler = new ProxyViewAssembler();
        return proxyViewAssembler.toModel(getProxy_withAuth());
    }

    private Proxy getProxy_withAuth() {
        final var proxyFactory = new ProxyFactory();
        return proxyFactory.create(getProxyDesc_withAuth());
    }

    private ProxyDesc getProxyDesc_withAuth() {
        final var desc = new ProxyDesc();
        desc.setLocation(URI.create("https://localhost"));
        desc.setExclusions(List.of("https://localhost:8081"));
        desc.setAuthentication(new AuthenticationDesc("a", "b"));

        return desc;
    }
}
