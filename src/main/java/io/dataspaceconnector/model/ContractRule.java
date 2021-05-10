package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

/**
 * A ContractRule defines a rule that should be enforced.
 */
@Entity
@Table(name = "contractrule")
@SQLDelete(sql = "UPDATE contractrule SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ContractRule extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The rule id on provider side.
     */
    private URI remoteId;

    /**
     * The title of the rule.
     */
    private String title;

    /**
     * The definition of the rule.
     **/
    @Lob
    private String value;

    /**
     * The contracts in which this rule is used.
     */
    @ManyToMany(mappedBy = "rules")
    private List<Contract> contracts;
}
