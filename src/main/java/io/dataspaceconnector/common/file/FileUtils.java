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
package io.dataspaceconnector.common.file;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Utility class for working with files.
 */
public final class FileUtils {

    /**
     * Constructor.
     */
    private FileUtils() {
        // not used
    }

    /**
     * Return a list of all files in a directory.
     *
     * @param directory The directory.
     * @return List of contained files.
     */
    public static File[] getContainedFiles(final File directory) {
        assert directory.isDirectory();
        final var out = directory.listFiles();
        return out == null ? new File[]{} : out;
    }

    /**
     * Open a file.
     *
     * @param path The filepath.
     * @return The file.
     * @throws FileNotFoundException if the file does not exist.
     */
    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    public static File openFile(final String path) throws FileNotFoundException {
        final var base = new File(path);
        if (!base.exists()) {
            throw new FileNotFoundException("File '" + path + "' does not exist.");
        }

        return base;
    }
}
