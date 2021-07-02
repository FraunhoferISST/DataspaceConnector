package io.dataspaceconnector.bootstrap;

import javax.annotation.PostConstruct;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Loads the bootstrapper on initialization.
 */
@ConditionalOnProperty(value = "bootstrap.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Service
public final class BootstrapLoader {
    private final @NonNull Bootstrapper bootstrapper;

    /**
     * Load the bootstrapper.
     */
    @PostConstruct
    public void loadBootstrapper() {
        bootstrapper.bootstrap();
    }
}
