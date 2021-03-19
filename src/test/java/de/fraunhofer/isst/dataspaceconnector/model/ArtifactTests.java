package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArtifactTests {

    @Test
    public void randomTests() {
        ArtifactDesc desc = new ArtifactDesc();
        ArtifactFactory factory = new ArtifactFactory();
        var artifact = factory.create(desc);

        final var before = artifact.getNumAccessed();

        artifact.incrementAccessCounter();

        assertEquals((long) artifact.getNumAccessed(), before + 1);
    }

//    @Test
//    public void setTest() {
//        ArtifactDesc desc = new ArtifactDesc();
//        desc.setTitle("Test");
//        ArtifactFactory factory = new ArtifactFactory();
//        var artifact = factory.create(desc);
//
//        artifact.setTitle("Different");
//        Assert.assertEquals(artifact.getTitle(), "Different");
//    }

    @Test
    public void toStringTest() {
        ArtifactDesc desc = new ArtifactDesc();
        ArtifactFactory factory = new ArtifactFactory();
        var artifact = factory.create(desc);

        var className = ((Artifact)artifact).getClass().getSimpleName() + "()";
        assertEquals(artifact.toString(), className);
    }
}
