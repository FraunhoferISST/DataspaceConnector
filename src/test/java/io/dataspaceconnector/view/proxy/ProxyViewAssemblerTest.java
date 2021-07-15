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
package io.dataspaceconnector.view.proxy;

import io.dataspaceconnector.model.proxy.Proxy;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class ProxyViewAssemblerTest {

    @Test
    public void create_ValidProxy_returnProxyView() {
        /* ARRANGE */
        final var shouldLookLike = getProxy();

        /* ACT */
        final var after = getProxyView();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
        assertFalse(after.getExclusions().isEmpty());
        assertEquals(after.getExclusions().get(0), shouldLookLike.getExclusions().get(0));
    }

    private Proxy getProxy() {
        final var proxyFactory = new ProxyFactory();
        return proxyFactory.create(getProxyDesc());
    }

    private ProxyDesc getProxyDesc() {
        final var desc = new ProxyDesc();
        desc.setLocation(URI.create("https://localhost"));
        desc.setExclusions(List.of("https://localhost:8081"));

        return desc;
    }

    private ProxyView getProxyView() {
        final var proxyViewAssembler = new ProxyViewAssembler();
        return proxyViewAssembler.toModel(getProxy());
    }
}
