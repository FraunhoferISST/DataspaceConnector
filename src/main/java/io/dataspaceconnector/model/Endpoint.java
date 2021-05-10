package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import java.net.URI;

@Entity
@Inheritance
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Table(name = "endpoint")
@RequiredArgsConstructor
public class Endpoint extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    private URI endpointDocumentation;

    private String endpointInformation;

    private String inboundPath;

    private String outboundPath;
}
