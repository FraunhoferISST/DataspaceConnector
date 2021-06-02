package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.Authentication;
import io.dataspaceconnector.model.AuthenticationDesc;
import io.dataspaceconnector.model.AuthenticationFactory;
import io.dataspaceconnector.repositories.AuthenticationRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {AuthenticationService.class})
public class AuthenticationServiceTest {

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @MockBean
    private AuthenticationFactory authenticationFactory;

    @Autowired
    @InjectMocks
    private AuthenticationService authenticationService;

    AuthenticationDesc authenticationDesc = getAuthenticationDesc();

    Authentication authentication = getAuthentication();

    UUID validId = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");

    List<Authentication> authenticationList = new ArrayList<>();

    /**********************************************************************
     * SETUP
     **********************************************************************/
    @BeforeEach
    public void init() {
        Mockito.when(authenticationFactory.create(any())).thenReturn(authentication);
        Mockito.when(authenticationRepository.saveAndFlush(Mockito.eq(authentication)))
                .thenReturn(authentication);
        Mockito.when(authenticationRepository.findById(Mockito.eq(authentication.getId())))
                .thenReturn(Optional.of(authentication));

        Mockito.when(authenticationRepository.saveAndFlush(Mockito.any()))
                .thenAnswer(this::saveAndFlushMock);
        Mockito.when(authenticationRepository.findAll(Pageable.unpaged()))
                .thenAnswer(this::findAllMock);
        Mockito.doThrow(InvalidDataAccessApiUsageException.class)
                .when(authenticationRepository)
                .deleteById(Mockito.isNull());
        Mockito.doAnswer(this::deleteByIdMock).when(authenticationRepository)
                .deleteById(Mockito.isA(UUID.class));
    }

    @SneakyThrows
    private Authentication saveAndFlushMock(final InvocationOnMock invocation) {
        final var obj = (Authentication) invocation.getArgument(0);
        final var idField = obj.getClass().getSuperclass().
                getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(obj, UUID.randomUUID());

        authenticationList.add(obj);
        return obj;
    }

    private static Page<Authentication> toPage(final List<Authentication> authenticationList,
                                               final Pageable pageable) {
        return new PageImpl<>(
                authenticationList.subList(0, authenticationList.size()),
                pageable, authenticationList.size());
    }

    private Page<Authentication> findAllMock(final InvocationOnMock invocation) {
        return toPage(authenticationList, invocation.getArgument(0));
    }

    private Answer<?> deleteByIdMock(final InvocationOnMock invocation) {
        final var obj = (UUID) invocation.getArgument(0);
        authenticationList.removeIf(x -> x.getId().equals(obj));
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
        assertThrows(IllegalArgumentException.class, () -> authenticationService.get(null));
    }

    @Test
    public void get_knownId_returnAuthentication() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = authenticationService.get(authentication.getId());

        /* ASSERT */
        assertEquals(authentication.getId(), result.getId());
        assertEquals(authentication.getUsername(), result.getUsername());
    }

    /**********************************************************************
     * CREATE
     **********************************************************************/
    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> authenticationService.create(null));
    }

    @Test
    public void create_ValidDesc_returnHasId() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var newAuthentication = authenticationService.create(authenticationDesc);

        /* ASSERT */
        assertEquals(authentication, newAuthentication);
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
                () -> authenticationService.update(authentication.getId(), null));
    }

    @Test
    public void update_NewDesc_returnUpdatedEntity() {
        /* ARRANGE */
        final var shouldLookLike = getAuthenticationFromValidDesc(validId,
                getNewAuthentication(getUpdatedAuthenticationDesc()));

        /* ACT */
        final var after =
                authenticationService.update(validId, getUpdatedAuthenticationDesc());

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
        assertThrows(IllegalArgumentException.class, () -> authenticationService.delete(null));
    }

    @Disabled
    @Test
    public void delete_knownId_removedObject() {
        /* ARRANGE */
        final var authentication = authenticationService.create(authenticationDesc);
        authenticationService.create(getUpdatedAuthenticationDesc());

        final var beforeCount = authenticationService.getAll(Pageable.unpaged()).getSize();

        /* ACT */
        authenticationService.delete(authentication.getId());

        /* ASSERT */
        assertEquals(beforeCount - 1, authenticationService.getAll(Pageable.unpaged()).getSize());
    }


    /**********************************************************************
     * UTILITIES
     **********************************************************************/
    @SneakyThrows
    private Authentication getAuthentication() {
        final var desc = getAuthenticationDesc();

        final var authenticationConstructor = Authentication.class.getConstructor();

        final var authentication = authenticationConstructor.newInstance();

        final var idField = authentication.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(authentication, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        final var usernameField = authentication.getClass().getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(authentication, desc.getUsername());

        return authentication;
    }

    private Authentication getNewAuthentication(final AuthenticationDesc updatedAuthenticationDesc) {
        return authenticationFactory.create(updatedAuthenticationDesc);
    }

    @SneakyThrows
    private Authentication getAuthenticationFromValidDesc(final UUID id,
                                                          final Authentication authentication) {
        final var idField = authentication.getClass().getSuperclass()
                .getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(authentication, id);

        return authentication;
    }

    private AuthenticationDesc getAuthenticationDesc() {
        final var desc = new AuthenticationDesc();
        desc.setUsername("user");

        return desc;
    }

    private AuthenticationDesc getUpdatedAuthenticationDesc() {
        final var desc = new AuthenticationDesc();
        desc.setUsername("new user");

        return desc;
    }
}
