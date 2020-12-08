package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * Creates example resources.
 */
public class ExampleResources {

    public static OfferedResource getExampleResource() {
        return new OfferedResource(
            UUIDUtils.createUUID((UUID x) -> false),
            new Date(),
            new Date(),
            getExampleMetadata(),
            getExampleData());
    }

    public static ResourceMetadata getExampleMetadata() {
        final var metadata = new ResourceMetadata();
        metadata.setRepresentations(
            Collections.singletonMap(
                UUIDUtils.createUUID((UUID x) -> false), getExampleResourceRepresentation()));
        metadata.setDescription("ExampleResourceDescription");
        metadata.setTitle("ExampleResource");
        metadata.setPolicy(getExamplePolicy());

        return metadata;
    }

    public static String getExamplePolicy() {
        return "Example policy";
    }

    public static String getExampleData() {
        return "<note>\n"
            + "<to>Everyone</to>\n"
            + "<from>Me</from>\n"
            + "<heading>Reminder</heading>\n"
            + "<body>Don't call!</body>\n"
            + "</note>";
    }

    public static ResourceRepresentation getExampleResourceRepresentation() {
        final var representation = new ResourceRepresentation();
        representation.setUuid(UUIDUtils.createUUID((UUID x) -> false));
        representation.setSource(getExampleBackendSource());
        representation.setName("Example Representation");
        representation.setByteSize(getExampleData().getBytes().length);
        representation.setType("XML");

        return representation;
    }

    public static BackendSource getExampleBackendSource() {
        final var source = new BackendSource();
        source.setType(BackendSource.Type.LOCAL);

        return source;
    }
}
