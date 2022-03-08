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
package io.dataspaceconnector.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import io.dataspaceconnector.common.net.HttpResponse;
import io.dataspaceconnector.common.net.HttpService;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.RouteDataRetriever;
import io.dataspaceconnector.common.routing.RouteResponse;
import io.dataspaceconnector.common.net.ApiReferenceHelper;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.Data;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.auth.Authentication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {DataRetriever.class})
class DataRetrieverTest {

    @MockBean
    private HttpService httpService;

    @MockBean
    private RouteDataRetriever routeDataRetriever;

    @MockBean
    private ApiReferenceHelper apiReferenceHelper;

    @Autowired
    private DataRetriever retriever;

    @Test
    @SneakyThrows
    void retrieveData_localData_returnData() {
        /* ARRANGE */
        final var data = getLocalData();
        final var artifact = getArtifact(data);

        /* ACT */
        final var result = retriever.retrieveData(artifact, null);

        /* ASSERT */
        assertNotNull(result);
        assertArrayEquals(data.getValue(), result.readAllBytes());
    }

    @Test
    @SneakyThrows
    void retrieveData_remoteData_returnData() {
        /* ARRANGE */
        final var data = getRemoteData();
        final var artifact = getArtifact(data);
        final var dataValue = "data".getBytes();
        final var response = new HttpResponse(200, new ByteArrayInputStream(dataValue));

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(false);
        when(httpService.get(data.getAccessUrl(), (QueryInput) null)).thenReturn(response);

        /* ACT */
        final var result = retriever.retrieveData(artifact, null);

        /* ASSERT */
        assertNotNull(result);
        assertArrayEquals(dataValue, result.readAllBytes());
    }

    @Test
    @SneakyThrows
    void retrieveData_remoteDataWithRoute_returnData() {
        /* ARRANGE */
        final var data = getRemoteData();
        final var artifact = getArtifact(data);
        final var dataValue = "data".getBytes();
        final var response = new RouteResponse(new ByteArrayInputStream(dataValue));

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);
        when(routeDataRetriever.get(data.getAccessUrl(), null)).thenReturn(response);

        /* ACT */
        final var result = retriever.retrieveData(artifact, null);

        /* ASSERT */
        assertNotNull(result);
        assertArrayEquals(dataValue, result.readAllBytes());
    }

    @Test
    void getData_withDataIsNull() throws Exception {
        var emptyData = new LocalData();
        var getDataMethod = retriever.getClass().getDeclaredMethod("getData", LocalData.class);
        getDataMethod.setAccessible(true);
        var emptyRes = (InputStream) getDataMethod.invoke(retriever, emptyData);
        assertTrue(emptyRes.readAllBytes().length == 0);
    }

    @Test
    void getData_withDataNotNull() throws Exception {
        var data = new LocalData();
        var value = new byte[]{1, 2, 3, 4};
        data.setValue(value);
        var getDataMethod = retriever.getClass().getDeclaredMethod("getData", LocalData.class);
        getDataMethod.setAccessible(true);
        var res = (InputStream) getDataMethod.invoke(retriever, data);
        assertTrue(Arrays.equals(res.readAllBytes(), value));
    }

    private ArtifactImpl getArtifact(final Data data) {
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(artifact, "data", data);
        return artifact;
    }

    @SneakyThrows
    private LocalData getLocalData() {
        final var dataConstructor = LocalData.class.getConstructor();
        dataConstructor.setAccessible(true);

        final var localData = dataConstructor.newInstance();

        final var idField = localData.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(localData, 1L);

        final var valueField = localData.getClass().getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(localData, "data".getBytes());

        return localData;
    }

    @SneakyThrows
    private RemoteData getRemoteData() {
        final var remoteData = new RemoteData();
        ReflectionTestUtils.setField(remoteData, "authentication", new ArrayList<Authentication>());
        ReflectionTestUtils.setField(remoteData, "accessUrl",  new URL("http://some-url.com/" + UUID.randomUUID()));
        return remoteData;
    }

}
