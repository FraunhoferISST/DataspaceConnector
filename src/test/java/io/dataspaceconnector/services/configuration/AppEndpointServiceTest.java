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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.model.endpoints.AppEndpoint;
import io.dataspaceconnector.model.endpoints.AppEndpointDesc;
import io.dataspaceconnector.model.endpoints.AppEndpointFactory;
import io.dataspaceconnector.repositories.AppEndpointRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {AppEndpointService.class})
public class AppEndpointServiceTest {

    @MockBean
    private AppEndpointRepository appEndpointRepository;

    @MockBean
    private AppEndpointFactory appEndpointFactory;

    @Autowired
    @InjectMocks
    private AppEndpointService appEndpointService;

    AppEndpointDesc appEndpointDesc = getAppEndpointDesc();
    AppEndpoint appEndpoint = getAppEndpoint();

    UUID validId = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");

    List<AppEndpoint> appEndpointList = new ArrayList<>();

    /**
     * SETUP
     */
    @BeforeEach
    public void init() {
        Mockito.when(appEndpointFactory.create(any())).thenReturn(appEndpoint);
        Mockito.when(appEndpointRepository.saveAndFlush(Mockito.eq(appEndpoint)))
                .thenReturn(appEndpoint);
        Mockito.when(appEndpointRepository.findById(Mockito.eq(appEndpoint.getId())))
                .thenReturn(Optional.of(appEndpoint));

        Mockito.when(appEndpointRepository.saveAndFlush(Mockito.any())).thenAnswer(this::saveAndFlushMock);
        Mockito.when(appEndpointRepository.findAll(Pageable.unpaged())).thenAnswer(this::findAllMock);
        Mockito.doThrow(InvalidDataAccessApiUsageException.class)
                .when(appEndpointRepository)
                .deleteById(Mockito.isNull());
        Mockito.doAnswer(this::deleteByIdMock).when(appEndpointRepository)
                .deleteById(Mockito.isA(UUID.class));
    }

    @SneakyThrows
    private AppEndpoint saveAndFlushMock(final InvocationOnMock invocation ) {
        final var obj = (AppEndpoint) invocation.getArgument(0);
        final var idField = obj.getClass().getSuperclass().getSuperclass().
                getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(obj, UUID.randomUUID());

        appEndpointList.add(obj);
        return obj;
    }

    private static Page<AppEndpoint> toPage(final List<AppEndpoint> appEndpointList,
                                            final Pageable pageable ) {
        return new PageImpl<>(
                appEndpointList.subList(0, appEndpointList.size()),
                pageable, appEndpointList.size());
    }

    private Page<AppEndpoint> findAllMock(final InvocationOnMock invocation) {
        return toPage(appEndpointList, invocation.getArgument(0));
    }

    private Answer<?> deleteByIdMock(final InvocationOnMock invocation ) {
        final var obj = (UUID) invocation.getArgument(0);
        appEndpointList.removeIf(x -> x.getId().equals(obj));
        return null;
    }

    /**********************************************************************
     * GET
     **********************************************************************/
    @Test
    public void get_nullId_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> appEndpointService.get(null));
    }

    @Test
    public void get_knownId_returnAppEndpoint() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = appEndpointService.get(appEndpoint.getId());

        /* ASSERT */
        assertEquals(appEndpoint.getId(), result.getId());
        assertEquals(appEndpoint.getName(), result.getName());
    }

    /**********************************************************************
     * CREATE
     **********************************************************************/
    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> appEndpointService.create(null));
    }

    @Test
    public void create_ValidDesc_returnHasId() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var newAppEndpoint = appEndpointService.create(appEndpointDesc);

        /* ASSERT */
        assertEquals(appEndpoint, newAppEndpoint);
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
                () -> appEndpointService.update(appEndpoint.getId(), null));
    }

    @Test
    public void update_NewDesc_returnUpdatedEntity() {
        /* ARRANGE */
        final var shouldLookLike = getAppEndpointFromValidDesc(validId,
                getNewAppEndpoint(getUpdatedAppEndpointDesc()));

        /* ACT */
        final var after =
                appEndpointService.update(validId, getUpdatedAppEndpointDesc());

        /* ASSERT */
        assertEquals(after, shouldLookLike);
    }

    /**********************************************************************
     * DELETE
     **********************************************************************/
    @Test
    public void delete_nullId_throwsIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> appEndpointService.delete(null));
    }

    @Disabled
    @Test
    public void delete_knownId_removedObject() {
        /* ARRANGE */
        final var appEndpoint = appEndpointService.create(appEndpointDesc);
        appEndpointService.create(getUpdatedAppEndpointDesc());

        final var beforeCount = appEndpointService.getAll(Pageable.unpaged()).getSize();

        /* ACT */
        appEndpointService.delete(appEndpoint.getId());

        /* ASSERT */
        assertEquals(beforeCount - 1, appEndpointService.getAll(Pageable.unpaged()).getSize());
    }

    /**********************************************************************
     * UTILITIES
     **********************************************************************/
    @SneakyThrows
    private AppEndpoint getAppEndpoint() {
        final var desc = getAppEndpointDesc();

        final var appEndpointConstructor = AppEndpoint.class.getConstructor();

        final var appEndpoint = appEndpointConstructor.newInstance();

        final var idField = appEndpoint.getClass().getSuperclass().getSuperclass()
                .getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(appEndpoint, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        final var accessUrlField = appEndpoint.getClass().getDeclaredField("name");
        accessUrlField.setAccessible(true);
        accessUrlField.set(appEndpoint, desc.getLocation());

        return appEndpoint;
    }

    private AppEndpoint getNewAppEndpoint(final AppEndpointDesc updatedAppEndpointDesc) {
        return appEndpointFactory.create(updatedAppEndpointDesc);
    }

    @SneakyThrows
    private AppEndpoint getAppEndpointFromValidDesc(final UUID id,
                                                    final AppEndpoint appEndpoint) {
        final var idField = appEndpoint.getClass().getSuperclass().getSuperclass()
                .getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(appEndpoint, id);

        return appEndpoint;
    }

    private AppEndpointDesc getAppEndpointDesc() {
        final var desc = new AppEndpointDesc();
        desc.setLocation(URI.create("https://appendpoint"));

        return desc;
    }

    private AppEndpointDesc getUpdatedAppEndpointDesc() {
        final var desc = new AppEndpointDesc();
        desc.setLocation(URI.create("https://updatedendpoint"));

        return desc;
    }
}
