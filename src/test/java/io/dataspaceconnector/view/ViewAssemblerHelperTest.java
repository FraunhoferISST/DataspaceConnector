package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.resources.ResourceControllers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ViewAssemblerHelper.class})
public class ViewAssemblerHelperTest {

    @Test
    public void getSelfLink_bothParametersNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> ViewAssemblerHelper.getSelfLink(null, null));
    }

    @Test
    public void getSelfLink_controllerClassNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> ViewAssemblerHelper.getSelfLink(UUID.randomUUID(), null));
    }

    @Test
    public void getSelfLink_entityIdNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = ViewAssemblerHelper.getSelfLink(null,
                ResourceControllers.ArtifactController.class);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void getSelfLink_inputCorrect_returnSelfLink() {
        /* ARRANGE */
        final var resourceId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = ViewAssemblerHelper.getSelfLink(resourceId,
                ResourceControllers.ArtifactController.class);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + resourceId, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

}
