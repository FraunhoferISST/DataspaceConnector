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

// import io.dataspaceconnector.model.endpoints.GenericEndpoint;
// import io.dataspaceconnector.model.endpoints.GenericEndpointDesc;
// import io.dataspaceconnector.model.endpoints.GenericEndpointFactory;
// import io.dataspaceconnector.repositories.GenericEndpointRepository;
// import lombok.SneakyThrows;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;

// import java.util.Optional;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;

// @SpringBootTest(classes = {GenericEndpointService.class})
// public class GenericEndpointServiceTest {

//     @MockBean
//     private GenericEndpointRepository genericEndpointRepository;

//     @MockBean
//     private GenericEndpointFactory genericEndpointFactory;

//     @Autowired
//     @InjectMocks
//     private GenericEndpointService genericEndpointService;

//     GenericEndpoint genericEndpoint = getGenericEndpoint();
//     GenericEndpointDesc genericEndpointDesc = getGenericEndpointDesc();

//     UUID validId = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");

//     /**********************************************************************
//      * SETUP
//      **********************************************************************/
//     @BeforeEach
//     public void init() {
//         Mockito.when(genericEndpointFactory.create(any())).thenReturn(genericEndpoint);
//         Mockito.when(genericEndpointRepository.saveAndFlush(Mockito.eq(genericEndpoint)))
//                 .thenReturn(genericEndpoint);
//         Mockito.when(genericEndpointRepository.findById(Mockito.eq(genericEndpoint.getId())))
//                 .thenReturn(Optional.of(genericEndpoint));
//     }

//     /**********************************************************************
//      * GET
//      **********************************************************************/
//     @Test
//     public void get_nullId_throwIllegalArgumentException() {
//         /* ARRANGE */
//         // Nothing to arrange here.

//         /* ACT && ASSERT */
//         assertThrows(IllegalArgumentException.class, () -> genericEndpointService.get(null));
//     }

//     @Test
//     public void get_knownId_returnGenericEndpoint() {
//         /* ARRANGE */
//         // Nothing to arrange here.

//         /* ACT */
//         final var result = genericEndpointService.get(genericEndpoint.getId());

//         /* ASSERT */
//         assertEquals(genericEndpoint.getId(), result.getId());
//         assertEquals(genericEndpoint.getAbsolutePath(), result.getAbsolutePath());
//     }

//     /**********************************************************************
//      * CREATE
//      **********************************************************************/
//     @Test
//     public void create_nullDesc_throwIllegalArgumentException() {
//         /* ARRANGE */

//         /* ACT && ASSERT */
//         assertThrows(IllegalArgumentException.class, () -> genericEndpointService.create(null));
//     }

//     @Test
//     public void create_ValidDesc_returnHasId() {
//         /* ARRANGE */
//         // Nothing to arrange here.

//         /* ACT */
//         final var newGenericEndpoint = genericEndpointService.create(genericEndpointDesc);

//         /* ASSERT */
//         assertEquals(genericEndpoint, newGenericEndpoint);
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
//                 () -> genericEndpointService.update(genericEndpoint.getId(), null));
//     }

//     @Test
//     public void update_NewDesc_returnUpdatedEntity() {
//         /* ARRANGE */
//         final var shouldLookLike = getGenericEndpointFromValidDesc(validId,
//                 getNewGenericEndpoint(getUpdatedGenericEndpointDesc()));

//         /* ACT */
//         final var after =
//                 genericEndpointService.update(validId, getUpdatedGenericEndpointDesc());

//         /* ASSERT */
//         assertEquals(after, shouldLookLike);
//     }

//     /**********************************************************************
//      * UTILITIES
//      **********************************************************************/
//     @SneakyThrows
//     private GenericEndpoint getGenericEndpoint() {
//         final var desc = getGenericEndpointDesc();

//         final var genericEndpointConstructor = GenericEndpoint.class.getDeclaredConstructor();

//         final var genericEndpoint = genericEndpointConstructor.newInstance();

//         final var idField = genericEndpoint.getClass().getSuperclass().
//                 getSuperclass().getDeclaredField("id");
//         idField.setAccessible(true);
//         idField.set(genericEndpoint, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

//         final var absolutePathField = genericEndpoint.getClass().getDeclaredField("absolutePath");
//         absolutePathField.setAccessible(true);
//         absolutePathField.set(genericEndpoint, desc.getAbsolutePath());

//         return genericEndpoint;
//     }

//     private GenericEndpoint getNewGenericEndpoint(final GenericEndpointDesc updatedGenericEndpointDesc) {
//         return genericEndpointFactory.create(updatedGenericEndpointDesc);
//     }

//     @SneakyThrows
//     private GenericEndpoint getGenericEndpointFromValidDesc(final UUID id,
//                                                             final GenericEndpoint genericEndpoint) {
//         final var idField = genericEndpoint.getClass()
//                 .getSuperclass().getSuperclass().getDeclaredField("id");
//         idField.setAccessible(true);
//         idField.set(genericEndpoint, id);

//         return genericEndpoint;
//     }

//     private GenericEndpointDesc getGenericEndpointDesc() {
//         final var desc = new GenericEndpointDesc();
//         desc.setAbsolutePath("https://endpoint");

//         return desc;
//     }

//     private GenericEndpointDesc getUpdatedGenericEndpointDesc() {
//         final var desc = new GenericEndpointDesc();
//         desc.setAbsolutePath("https://newendpoint");

//         return desc;
//     }
// }
