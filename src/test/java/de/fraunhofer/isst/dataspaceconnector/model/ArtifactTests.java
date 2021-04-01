package de.fraunhofer.isst.dataspaceconnector.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArtifactTests {

    @Test
    public void randomTests() {
        ArtifactDesc desc = new ArtifactDesc();
        ArtifactFactory factory = new ArtifactFactory();
        var artifact = factory.create(desc);

        artifact.incrementAccessCounter();

        assertEquals(1, artifact.getNumAccessed());
    }
}
