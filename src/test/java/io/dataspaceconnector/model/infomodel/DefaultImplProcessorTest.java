package io.dataspaceconnector.model.infomodel;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultImplProcessorTest {
    @TempDir
    Path tempDir;

    @Test
    public void test() throws IOException {
        // Given
        var compiler = ToolProvider.getSystemJavaCompiler();

        var diagnostics = new DiagnosticCollector<JavaFileObject>();

        var fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempDir.toFile()));
        fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(tempDir.toFile()));

        var file = new File("src/main/java/io/dataspaceconnector/model/infomodel/Definitions.java");
        var compilationUnits = fileManager.getJavaFileObjects(file);

        var task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
        task.setProcessors(Collections.singletonList(new DefaultImplProcessor()));

        // When
        boolean success = task.call();

        // Then
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.err.println(diagnostic);
        }

        assertTrue(success);
    }
}
