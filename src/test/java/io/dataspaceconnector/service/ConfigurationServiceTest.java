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
// /*
//  * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
//  *
//  * Licensed under the Apache License, Version 2.0 (the "License");
//  * you may not use this file except in compliance with the License.
//  * You may obtain a copy of the License at
//  *
//  *    http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.
//  */
// package io.dataspaceconnector.services.configuration;

// import io.dataspaceconnector.model.configurations.Configuration;
// import io.dataspaceconnector.model.configurations.ConfigurationDesc;
// import io.dataspaceconnector.model.configurations.ConfigurationFactory;
// import io.dataspaceconnector.model.configurations.DeployMode;
// import io.dataspaceconnector.repositories.ConfigurationRepository;
// import lombok.SneakyThrows;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Disabled;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mockito;
// import org.mockito.invocation.InvocationOnMock;
// import org.mockito.stubbing.Answer;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.dao.InvalidDataAccessApiUsageException;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.Pageable;

// import java.common.ArrayList;
// import java.common.List;
// import java.common.Optional;
// import java.common.UUID;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;

// @SpringBootTest(classes = {ConfigurationService.class})
// public class ConfigurationServiceTest {

//     @MockBean
//     private ConfigurationRepository configurationRepository;

//     @MockBean
//     private ConfigurationFactory configurationFactory;

//     @Autowired
//     @InjectMocks
//     private ConfigurationService configurationService;

//     ConfigurationDesc configurationDesc = getConfigurationDesc();

//     Configuration configuration = getConfiguration();

//     UUID validId = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");

//     List<Configuration> configurationList = new ArrayList<>();

//     /**********************************************************************
//      * SETUP
//      **********************************************************************/
//     @BeforeEach
//     public void init() {
//         Mockito.when(configurationFactory.create(any())).thenReturn(configuration);
//         Mockito.when(configurationRepository.saveAndFlush(Mockito.eq(configuration)))
//                 .thenReturn(configuration);
//         Mockito.when(configurationRepository.findById(Mockito.eq(configuration.getId())))
//                 .thenReturn(Optional.of(configuration));

//         Mockito.when(configurationRepository.saveAndFlush(Mockito.any()))
//                 .thenAnswer(this::saveAndFlushMock);
//         Mockito.when(configurationRepository.findAll(Pageable.unpaged()))
//                 .thenAnswer(this::findAllMock);
//         Mockito.doThrow(InvalidDataAccessApiUsageException.class)
//                 .when(configurationRepository)
//                 .deleteById(Mockito.isNull());
//         Mockito.doAnswer(this::deleteByIdMock).when(configurationRepository)
//                 .deleteById(Mockito.isA(UUID.class));
//     }

//     @SneakyThrows
//     private Configuration saveAndFlushMock(final InvocationOnMock invocation) {
//         final var obj = (Configuration) invocation.getArgument(0);
//         final var idField = obj.getClass().getSuperclass().
//                 getDeclaredField("id");
//         idField.setAccessible(true);
//         idField.set(obj, UUID.randomUUID());

//         configurationList.add(obj);
//         return obj;
//     }

//     private static Page<Configuration> toPage(final List<Configuration> configurationList,
//                                                final Pageable pageable) {
//         return new PageImpl<>(
//                 configurationList.subList(0, configurationList.size()),
//                 pageable, configurationList.size());
//     }

//     private Page<Configuration> findAllMock(final InvocationOnMock invocation) {
//         return toPage(configurationList, invocation.getArgument(0));
//     }

//     private Answer<?> deleteByIdMock(final InvocationOnMock invocation) {
//         final var obj = (UUID) invocation.getArgument(0);
//         configurationList.removeIf(x -> x.getId().equals(obj));
//         return null;
//     }

//     /**********************************************************************
//      * GET
//      **********************************************************************/
//     @Test
//     public void get_nullId_throwIllegalArgumentException() {
//         /* ARRANGE */
//         // Nothing to arrange here.

//         /* ACT && ASSERT */
//         assertThrows(IllegalArgumentException.class, () -> configurationService.get(null));
//     }

//     @Test
//     public void get_knownId_returnConfiguration() {
//         /* ARRANGE */
//         // Nothing to arrange here.

//         /* ACT */
//         final var result = configurationService.get(configuration.getId());

//         /* ASSERT */
//         assertEquals(configuration.getId(), result.getId());
//         assertEquals(configuration.getDeployMode(), result.getDeployMode());
//     }

//     /**********************************************************************
//      * CREATE
//      **********************************************************************/
//     @Test
//     public void create_nullDesc_throwIllegalArgumentException() {
//         /* ARRANGE */

//         /* ACT && ASSERT */
//         assertThrows(IllegalArgumentException.class, () -> configurationService.create(null));
//     }

//     @Test
//     public void create_ValidDesc_returnHasId() {
//         /* ARRANGE */
//         // Nothing to arrange here.

//         /* ACT */
//         final var newConfiguration = configurationService.create(configurationDesc);

//         /* ASSERT */
//         assertEquals(configuration, newConfiguration);
//     }

//     /**********************************************************************
//      * UPDATE
//      **********************************************************************/
//     @Test
//     public void update_nullDesc_throwIllegalArgumentException() {
//         /* ARRANGE */
//         // Nothing to arrange here.

//         /* ACT && ASSERT */
//         assertThrows(IllegalArgumentException.class,
//                 () -> configurationService.update(configuration.getId(), null));
//     }

//     @Test
//     public void update_NewDesc_returnUpdatedEntity() {
//         /* ARRANGE */
//         final var shouldLookLike = getConfigurationFromValidDesc(validId,
//                 getNewConfiguration(getUpdatedConfigurationDesc()));

//         /* ACT */
//         final var after =
//                 configurationService.update(validId, getUpdatedConfigurationDesc());

//         /* ASSERT */
//         assertEquals(after, shouldLookLike);
//     }

//     /**********************************************************************
//      * DELETE
//      **********************************************************************/
//     @Test
//     public void delete_nullId_throwsIllegalArgumentException() {
//         /* ARRANGE */

//         /* ACT && ASSERT */
//         assertThrows(IllegalArgumentException.class, () -> configurationService.delete(null));
//     }

//     @Disabled
//     @Test
//     public void delete_knownId_removedObject() {
//         /* ARRANGE */
//         final var configuration = configurationService.create(configurationDesc);
//         configurationService.create(getUpdatedConfigurationDesc());

//         final var beforeCount = configurationService.getAll(Pageable.unpaged()).getSize();

//         /* ACT */
//         configurationService.delete(configuration.getId());

//         /* ASSERT */
//         assertEquals(beforeCount - 1, configurationService.getAll(Pageable.unpaged()).getSize());
//     }

//     /**********************************************************************
//      * UTILITIES
//      **********************************************************************/
//     @SneakyThrows
//     private Configuration getConfiguration() {
//         final var desc = getConfigurationDesc();

//         final var configurationConstructor = Configuration.class.getConstructor();

//         final var configuration = configurationConstructor.newInstance();

//         final var idField = configuration.getClass().getSuperclass().getDeclaredField("id");
//         idField.setAccessible(true);
//         idField.set(configuration, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

//         final var deployModeField = configuration.getClass().getDeclaredField("deployMode");
//         deployModeField.setAccessible(true);
//         deployModeField.set(configuration, desc.getDeployMode());

//         return configuration;
//     }

//     private Configuration getNewConfiguration(final ConfigurationDesc updatedConfigurationDesc) {
//         return configurationFactory.create(updatedConfigurationDesc);
//     }

//     @SneakyThrows
//     private Configuration getConfigurationFromValidDesc(final UUID id,
//                                                         final Configuration configuration) {
//         final var idField = configuration.getClass().getSuperclass()
//                 .getDeclaredField("id");
//         idField.setAccessible(true);
//         idField.set(configuration, id);

//         return configuration;
//     }

//     private ConfigurationDesc getConfigurationDesc() {
//         final var desc = new ConfigurationDesc();
//         desc.setDeployMode(DeployMode.TEST);

//         return desc;
//     }

//     private ConfigurationDesc getUpdatedConfigurationDesc() {
//         final var desc = new ConfigurationDesc();
//         desc.setDeployMode(DeployMode.PRODUCTIVE);

//         return desc;
//     }
// }
