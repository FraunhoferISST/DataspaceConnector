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
package io.dataspaceconnector.service.resource.type;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.exception.SubscriptionProcessingException;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.model.subscription.SubscriptionFactory;
import io.dataspaceconnector.repository.SubscriptionRepository;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.resource.relation.ArtifactSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.OfferedResourceSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.RepresentationArtifactLinker;
import io.dataspaceconnector.service.resource.relation.RequestedResourceSubscriptionLinker;
import io.dataspaceconnector.service.resource.spring.ServiceLookUp;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubscriptionServiceTest {

    private SubscriptionRepository repository = Mockito.mock(SubscriptionRepository.class);
    private SubscriptionFactory factory = Mockito.mock(SubscriptionFactory.class);

    private ArtifactSubscriptionLinker artSubLinker = Mockito.mock(ArtifactSubscriptionLinker.class);
    private RepresentationArtifactLinker repSubLinker = Mockito.mock(RepresentationArtifactLinker.class);
    private OfferedResourceSubscriptionLinker   offeredResLinker = Mockito.mock(OfferedResourceSubscriptionLinker.class);
    private RequestedResourceSubscriptionLinker reqSubLinker     = Mockito.mock(RequestedResourceSubscriptionLinker.class);
    private EntityResolver                      entityResolver   = Mockito.mock(EntityResolver.class);
    private ServiceLookUp lookUp = Mockito.mock(ServiceLookUp.class);

    private SubscriptionService service = Mockito.spy(new SubscriptionService(repository, factory,
                                                                  entityResolver, lookUp));

    SubscriptionDesc subscriptionDesc = getDesc();
    Subscription subscription = getSubscription();
    List<Subscription> subscriptionList = new ArrayList<>();

    @BeforeEach
    public void init() {
        Mockito.when(factory.create(subscriptionDesc)).thenReturn(subscription);

        Mockito.when(repository.findById(Mockito.eq(subscription.getId())))
                .thenReturn(Optional.of(subscription));
        Mockito.when(repository.saveAndFlush(Mockito.eq(subscription))).thenReturn(subscription);

        Mockito.when(repository.saveAndFlush(Mockito.any())).thenAnswer(this::saveAndFlushMock);
        Mockito.when(repository.findById(AdditionalMatchers.not(Mockito.eq(subscription.getId()))))
                .thenReturn(Optional.empty());
        Mockito.when(repository.findById(Mockito.isNull()))
                .thenThrow(InvalidDataAccessApiUsageException.class);
        Mockito.when(repository.findAll(Pageable.unpaged())).thenAnswer(this::findAllMock);
        Mockito.doThrow(InvalidDataAccessApiUsageException.class)
                .when(repository)
                .deleteById(Mockito.isNull());
        Mockito.doAnswer(this::deleteByIdMock).when(repository).deleteById(Mockito.isA(UUID.class));

        Mockito.doReturn(Optional.of(artSubLinker)).when(lookUp).getService(ArtifactSubscriptionLinker.class);
        Mockito.doReturn(Optional.of(repSubLinker)).when(lookUp).getService(RepresentationArtifactLinker.class);
        Mockito.doReturn(Optional.of(offeredResLinker)).when(lookUp).getService(OfferedResourceSubscriptionLinker.class);
        Mockito.doReturn(Optional.of(reqSubLinker)).when(lookUp).getService(RequestedResourceSubscriptionLinker.class);
    }

    @Test
    public void create_inputNull_throwIllegalArgumentException() {
        /* ARRANGE */
        // nothing to arrange here

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    public void create_emptyInput_throwIllegalArgumentException() {
        /* ARRANGE */
        // nothing to arrange here

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.create(new SubscriptionDesc()));
    }

    @Test
    public void create_validInputInvalidTarget_throwResourceNotFoundException() {
        /* ARRANGE */
        // nothing to arrange here

        /* ACT & ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> service.create(subscriptionDesc));
    }

    @Test
    public void create_validInputInvalidTargetType_throwSubscriptionProcessingException() {
        /* ARRANGE */
        Mockito.doReturn(Optional.of(subscription)).when(entityResolver).getEntityById(Mockito.any());

        /* ACT & ASSERT */
        assertThrows(SubscriptionProcessingException.class, () -> service.create(subscriptionDesc));
    }

    @Test
    public void create_validInput_returnSubscriptionForArtifact() {
        /* ARRANGE */
        Mockito.doReturn(Optional.of(getArtifact())).when(entityResolver).getEntityById(Mockito.any());
        Mockito.doNothing().when(artSubLinker).add(Mockito.any(), Mockito.any());

        /* ACT */
        final var subscription = service.create(subscriptionDesc);

        /* ASSERT */
        assertNotNull(subscription);
    }

    @Test
    public void create_validInput_returnSubscriptionForRepresentation() {
        /* ARRANGE */
        Mockito.doReturn(Optional.of(getRepresentation())).when(entityResolver).getEntityById(Mockito.any());
        Mockito.doNothing().when(repSubLinker).add(Mockito.any(), Mockito.any());

        /* ACT */
        final var subscription = service.create(subscriptionDesc);

        /* ASSERT */
        assertNotNull(subscription);
    }

    @Test
    public void create_validInput_returnSubscriptionForOffer() {
        /* ARRANGE */
        Mockito.doReturn(Optional.of(getOfferedResource())).when(entityResolver).getEntityById(Mockito.any());
        Mockito.doNothing().when(offeredResLinker).add(Mockito.any(), Mockito.any());

        /* ACT */
        final var subscription = service.create(subscriptionDesc);

        /* ASSERT */
        assertNotNull(subscription);
    }

    @Test
    public void create_validInput_returnSubscriptionForRequest() {
        /* ARRANGE */
        Mockito.doReturn(Optional.of(getRequestedResource())).when(entityResolver).getEntityById(Mockito.any());
        Mockito.doNothing().when(reqSubLinker).add(Mockito.any(), Mockito.any());

        /* ACT */
        final var subscription = service.create(subscriptionDesc);

        /* ASSERT */
        assertNotNull(subscription);
    }

    @Test
    public void getBySubscriber_validInput_returnSubscriptionList() {
        /* ARRANGE */
        final var pageable = Utils.toPageRequest(0, 30);
        Mockito.doReturn(List.of(subscription)).when(repository).findAllBySubscriber(Mockito.any());

        /* ACT */
        final var result = service.getBySubscriber(pageable, URI.create("https://subscriber"));

        /* ASSERT */
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(subscription, result.get(0));
    }

    @Test
    public void getBySubscriberAndTarget_validInput_returnSubscriptionList() {
        /* ARRANGE */
        Mockito.doReturn(List.of(subscription)).when(repository).findAllBySubscriberAndTarget(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = service.getBySubscriberAndTarget(URI.create("https://subscriber"), URI.create("https://target"));

        /* ASSERT */
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(subscription, result.get(0));
    }

    @Test
    public void getByTarget_validInput_returnSubscriptionList() {
        /* ARRANGE */
        Mockito.doReturn(List.of(subscription)).when(repository).findAllByTarget(Mockito.any());

        /* ACT */
        final var result = service.getByTarget(URI.create("https://target"));

        /* ASSERT */
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(subscription, result.get(0));
    }

    @Test
    public void removeSubscription_invalidTarget_throwResourceNotFoundException() {
        /* ARRANGE */
        // nothing to arrange here

        /* ACT & ASSERT */
        assertThrows(ResourceNotFoundException.class,
                () -> service.removeSubscription(URI.create("https://target"), URI.create("https://issuer")));
    }

    @Test
    public void removeSubscription_validInput_throwSubscriptionProcessingException() {
        /* ARRANGE */
        Mockito.doReturn(List.of(getSubscription())).when(service).getBySubscriberAndTarget(Mockito.any(), Mockito.any());
        Mockito.doThrow(new ResourceNotFoundException("")).when(service).delete(Mockito.any());

        /* ACT & ASSERT */
        assertThrows(SubscriptionProcessingException.class,
                () -> service.removeSubscription(URI.create("https://target"), URI.create("https://issuer")));
    }

    private SubscriptionDesc getDesc() {
        var desc = new SubscriptionDesc();
        desc.setTitle("The new title.");
        desc.setDescription("Description.");
        desc.setIdsProtocol(true);
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));
        desc.setPushData(false);

        return desc;
    }

    @SneakyThrows
    private Subscription getSubscription() {
        final var desc = getDesc();

        final var constructor = Subscription.class.getConstructor();

        final var subscription = constructor.newInstance();
        ReflectionTestUtils.setField(subscription, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));
        ReflectionTestUtils.setField(subscription, "title", desc.getTitle());

        return subscription;
    }

    private static Page<Subscription> toPage(final List<Subscription> list, final Pageable pageable) {
        return new PageImpl<>(list.subList(0, list.size()), pageable, list.size());
    }

    private Page<Subscription> findAllMock(final InvocationOnMock invocation) {
        return toPage(subscriptionList, invocation.getArgument(0));
    }

    @SneakyThrows
    private Subscription saveAndFlushMock(final InvocationOnMock invocation) {
        final var obj = (Subscription) invocation.getArgument(0);
        ReflectionTestUtils.setField(obj, "id", UUID.randomUUID());

        subscriptionList.add(obj);
        return obj;
    }

    private Answer<?> deleteByIdMock(final InvocationOnMock invocation) {
        final var obj = (UUID) invocation.getArgument(0);
        subscriptionList.removeIf(x -> x.getId().equals(obj));
        return null;
    }

    @SneakyThrows
    private ArtifactImpl getArtifact() {
        final var constructor = ArtifactImpl.class.getConstructor();
        constructor.setAccessible(true);

        final var artifact = constructor.newInstance();
        ReflectionTestUtils.setField(artifact, "title", "Artifact");
        ReflectionTestUtils.setField(artifact, "id", UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return artifact;
    }

    @SneakyThrows
    private Representation getRepresentation() {
        final var constructor = Representation.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var representation = constructor.newInstance();
        ReflectionTestUtils.setField(representation, "title", "Hello");
        ReflectionTestUtils.setField(representation, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return representation;
    }

    @SneakyThrows
    private OfferedResource getOfferedResource() {
        final var constructor = OfferedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var resource = constructor.newInstance();
        ReflectionTestUtils.setField(resource, "title", "Hello");
        ReflectionTestUtils.setField(resource, "representations", new ArrayList<Contract>());
        ReflectionTestUtils.setField(resource, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return resource;
    }

    @SneakyThrows
    private RequestedResource getRequestedResource() {
        final var constructor = RequestedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var resource = constructor.newInstance();
        ReflectionTestUtils.setField(resource,"title", "Hello");
        ReflectionTestUtils.setField(resource,"id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return resource;
    }

}
