package io.dataspaceconnector.telemetry.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.opentracing.noop.NoopTracer;

public class TelemetryConfigTest {
    private TelemetryConfig config = new TelemetryConfig();

    @Test
    public void jaegerTracer_is_NoopTracer() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = config.jaegerTracer();

        /* ASSERT */
        assertTrue(result instanceof NoopTracer);
    }
}
