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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.exception.SubscriptionProcessingException;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.model.subscription.SubscriptionFactory;
import io.dataspaceconnector.repository.SubscriptionRepository;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.resource.relation.ArtifactSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.OfferedResourceSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.RepresentationSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.RequestedResourceSubscriptionLinker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {SubscriptionService.class})
public class SubscriptionServiceTest {

    @MockBean
    private SubscriptionRepository repository;

    @MockBean
    private SubscriptionFactory factory;

    @MockBean
    private ArtifactSubscriptionLinker artSubLinker;

    @MockBean
    private RepresentationSubscriptionLinker repSubLinker;

    @MockBean
    private RequestedResourceSubscriptionLinker requestSubLinker;

    @MockBean
    private OfferedResourceSubscriptionLinker offerSubLinker;

    @MockBean
    private EntityResolver entityResolver;

    @Autowired
    private SubscriptionService service;

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
        // nothing to arrange here
        Mockito.doReturn(Optional.of(subscription)).when(entityResolver).getEntityById(Mockito.any());

        /* ACT & ASSERT */
        assertThrows(SubscriptionProcessingException.class, () -> service.create(subscriptionDesc));
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


//    @Test
//    public void getAllByContract_inputNull_throwIllegalArgumentException() {
//        /* ACT && ASSERT */
//        assertThrows(IllegalArgumentException.class, () -> service.getAllByContract(null));
//    }
//
//    @Test
//    public void getAllByContract_validInput_returnRules() {
//        /* ARRANGE */
//        final var contractId = UUID.randomUUID();
//        final var rules = List.of(new ContractRule());
//
//        when(repository.findAllByContract(contractId)).thenReturn(rules);
//
//        /* ACT */
//        final var result = service.getAllByContract(contractId);
//
//        /* ASSERT */
//        assertEquals(rules, result);
//    }
}
