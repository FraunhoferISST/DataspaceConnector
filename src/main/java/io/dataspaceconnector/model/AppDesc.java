package io.dataspaceconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Describes an app's properties.
 */
@Data
@NoArgsConstructor
public class AppDesc extends AbstractDescription<App> {

    /**
     * The title of the app.
     */
    private String title;
}
