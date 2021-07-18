package io.dataspaceconnector.model;

import io.dataspaceconnector.exception.InvalidEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubscriptionFactoryTest {

    private SubscriptionFactory factory;

    private URI newUrl = URI.create("http://valid-url.com");

    private URI initialUrl = URI.create("http://valid-url2.com");

    @BeforeEach
    public void init() {
        this.factory = new SubscriptionFactory();
    }

    @Test
    public void create_descNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    public void create_validDesc_returnSubscriberWithoutIdDatesAndResources() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(Subscription.class, result.getClass());
        assertEquals(desc.getUrl(), result.getUrl());
        assertEquals(desc.getSubscriber(), result.getSubscriber());
        assertEquals(desc.getTarget(), result.getTarget());
        assertEquals(desc.isIdsProtocol(), result.isIdsProtocol());
        assertEquals(desc.isPushData(), result.isPushData());
        assertNull(result.getCreationDate());
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_descUrlNull_throwInvalidEntityException() {
        /* ACT  && ASSERT */
        assertThrows(InvalidEntityException.class, () -> factory.create(new SubscriptionDesc()));
    }

    @Test
    public void update_subscriberNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> factory.update(null, new SubscriptionDesc()));
    }

    @Test
    public void update_descNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> factory.update(getSubscriber(), null));
    }

    @Test
    public void update_newUrl_returnTrueAndSubscriberUpdates() {
        /* ACT */
        final var subscriber = getSubscriber();
        final var desc = getValidDesc();

        final var url = subscriber.getUrl();

        /* ACT */
        factory.update(subscriber, desc);

        /* ASSERT */
        assertNotEquals(url, subscriber.getUrl());
        assertEquals(desc.getUrl(), subscriber.getUrl());
    }

    @Test
    public void update_descUrlNull_throwInvalidEntityException() {
        /* ACT  && ASSERT */
        assertThrows(InvalidEntityException.class,
                () -> factory.update(getSubscriber(), new SubscriptionDesc()));
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private SubscriptionDesc getValidDesc() {
        final var desc = new SubscriptionDesc();
        desc.setTarget(newUrl);
        desc.setUrl(newUrl);
        desc.setSubscriber(newUrl);
        desc.setPushData(true);
        desc.setIdsProtocol(false);
        return desc;
    }

    private Subscription getSubscriber() {
        final var subscriber = new Subscription();
        ReflectionTestUtils.setField(subscriber, "url", initialUrl);
        return subscriber;
    }

}
