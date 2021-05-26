package io.dataspaceconnector.model;

import io.dataspaceconnector.model.utils.UriConverter;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Apps can be downloaded from an app store to perform data operations on the data.
 */
@Entity
@Table(name = "appstore")
@SQLDelete(sql = "UPDATE appstore SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AppStore extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The access url of the app store.
     */
    @Convert(converter = UriConverter.class)
    private URI accessUrl;

    /**
     * The title of the app store.
     */
    private String title;

    /**
     * The registration status.
     */
    @Enumerated(EnumType.STRING)
    private RegisterStatus registerStatus;

    /**
     * The list of apps.
     */
    @OneToMany
    private List<App> appList;

    /**
     * The date specification.
     */
    private ZonedDateTime lastSeen;

}
