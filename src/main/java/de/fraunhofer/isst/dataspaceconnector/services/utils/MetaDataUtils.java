package de.fraunhofer.isst.dataspaceconnector.services.utils;

import java.util.Optional;

public final class MetaDataUtils {
    private MetaDataUtils(){
        
    }

    public static Optional<String> updateString(final String oldTitle,
                                                final String newTitle,
                                                final String defaultTitle) {
        final var newValue = newTitle == null ? defaultTitle : newTitle;
        if (oldTitle == null || !oldTitle.equals(newValue)) {
            return Optional.of(newValue);
        }

        return Optional.empty();
    }
}
