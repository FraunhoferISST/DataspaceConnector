package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.DutyImpl;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionImpl;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.ProhibitionImpl;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import de.fraunhofer.isst.ids.framework.util.IDSUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PolicyManagementService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyManagementService.class);

    /**
     * Service for current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Service for deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for contract agreements.
     */
    private final @NonNull AgreementService agreementService;

    /**
     * Service for artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Read and validate ids contract agreement from ids response message.
     *
     * @param response The response map containing ids header and payload.
     * @param request  The contract request that was sent.
     * @return The ids contract agreement.
     * @throws MessageResponseException If the response could not be processed.
     * @throws IllegalArgumentException If deserialization fails.
     * @throws ContractException        If the contract's content is invalid.
     */
    public ContractAgreement readAndValidateAgreementFromResponse(final Map<String, String> response,
                                                                  final ContractRequest request)
            throws MessageResponseException, IllegalArgumentException, ContractException {
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(response);
        final var agreement = deserializationService.getContractAgreement(payload);
        return validateContractAgreement(request, agreement);
    }

    /**
     * Get stored contract agreement for requested element.
     *
     * @param target The requested element.
     * @return The respective contract agreement.
     */
    public List<ContractAgreement> getContractAgreementsByTarget(final URI target) {
        final var uuid = EndpointUtils.getUUIDFromPath(target);
        final var artifact = artifactService.get(uuid);

        final var agreements = artifact.getAgreements();
        final var agreementList = new ArrayList<ContractAgreement>();
        for (final var agreement : agreements) {
            final var value = agreement.getValue();
            final var idsAgreement = deserializationService.getContractAgreement(value);
            agreementList.add(idsAgreement);
        }
        return agreementList;
    }

    /**
     * Build contract request from a list of rules - with assignee and consumer.
     *
     * @param ruleList The rule list.
     * @return The ids contract request.
     * @throws ConstraintViolationException If ids contract building fails.
     */
    public ContractRequest buildContractRequest(final List<? extends Rule> ruleList)
            throws ConstraintViolationException {
        final var connectorId = connectorService.getConnectorId();

        final var permissions = new ArrayList<Permission>();
        final var prohibitions = new ArrayList<Prohibition>();
        final var obligations = new ArrayList<Duty>();

        // Add assignee to all rules.
        for (final var rule : ruleList) {
            if (rule instanceof Permission) {
                ((PermissionImpl) rule).setAssignee(Util.asList(connectorId));
                permissions.add((Permission) rule);
            } else if (rule instanceof Prohibition) {
                ((ProhibitionImpl) rule).setAssignee(Util.asList(connectorId));
                prohibitions.add((Prohibition) rule);
            } else if (rule instanceof Duty) {
                ((DutyImpl) rule).setAssignee(Util.asList(connectorId));
                obligations.add((Duty) rule);
            }
        }

        // Return contract request.
        return new ContractRequestBuilder()
                ._consumer_(connectorId)
                ._obligation_(obligations)
                ._permission_(permissions)
                ._prohibition_(prohibitions)
                .build();
    }

    /**
     * Build contract agreement from contract request. Sign all rules as assigner.
     *
     * @param request The contract request.
     * @return The contract agreement.
     * @throws ConstraintViolationException If building a contract agreement fails.
     */
    public ContractAgreement buildContractAgreement(final ContractRequest request) throws ConstraintViolationException {
        final var connectorId = connectorService.getConnectorId();

        final var ruleList = PolicyUtils.extractRulesFromContract(request);

        final var permissions = new ArrayList<Permission>();
        final var prohibitions = new ArrayList<Prohibition>();
        final var obligations = new ArrayList<Duty>();

        // Add assigner to all rules.
        for (final var rule : ruleList) {
            if (rule instanceof Permission) {
                ((PermissionImpl) rule).setAssigner(Util.asList(connectorId));
                permissions.add((Permission) rule);
            } else if (rule instanceof Prohibition) {
                ((ProhibitionImpl) rule).setAssigner(Util.asList(connectorId));
                prohibitions.add((Prohibition) rule);
            } else if (rule instanceof Duty) {
                ((DutyImpl) rule).setAssigner(Util.asList(connectorId));
                obligations.add((Duty) rule);
            }
        }

        // Return contract request.
        return new ContractAgreementBuilder()
                ._consumer_(request.getId())
                ._contractDate_(IDSUtils.getGregorianNow())
                ._contractStart_(IDSUtils.getGregorianNow())
                ._contractEnd_(request.getContractEnd()) // TODO Improve calculation of contract
                // end.
                ._obligation_(obligations)
                ._permission_(permissions)
                ._prohibition_(prohibitions)
                ._provider_(connectorId)
                .build();
    }

    /**
     * Validate the content if the received contract agreement.
     *
     * @param request   The sent request.
     * @param agreement The contract agreement.
     * @return The ids contract agreement.
     * @throws ContractException If the content does not match the original request.
     */
    private ContractAgreement validateContractAgreement(final ContractRequest request,
                                                        final ContractAgreement agreement) throws ContractException {
        PolicyUtils.validateRuleAssigner(agreement);
        PolicyUtils.validateRuleContent(request, agreement);

        return agreement;
    }

    /**
     * Save contract agreement to database (consumer side).
     *
     * @param contractAgreement The ids contract agreement.
     * @param confirmed         Indicates whether both parties have agreed.
     * @return The id of the stored contract agreement.
     * @throws PersistenceException If the contract agreement could not be saved.
     */
    public URI saveContractAgreement(final ContractAgreement contractAgreement,
                                     final boolean confirmed) throws PersistenceException {
        try {
            final var agreementId = contractAgreement.getId();
            final var rdf = IdsUtils.toRdf(contractAgreement);

            final var desc = new AgreementDesc();
            desc.setRemoteId(agreementId); // Necessary on consumer side.
            desc.setConfirmed(confirmed);
            desc.setValue(rdf);

            // Save agreement to return its id.
            final var agreement = agreementService.create(desc);
            return EndpointUtils.getSelfLink(agreement);
        } catch (Exception e) {
            LOGGER.warn("Could not store contract agreement. [exception=({})]", e.getMessage());
            throw new PersistenceException("Could not store contract agreement.", e);
        }
    }

    /**
     * Save contract agreement to database with relation to targeted artifacts (provider side).
     *
     * @param contractAgreement The ids contract agreement.
     * @param confirmed         Indicates whether both parties have agreed.
     * @param targetList        List of artifacts.
     * @return The id of the stored contract agreement.
     * @throws PersistenceException If the contract agreement could not be saved.
     */
    public URI saveContractAgreement(final ContractAgreement contractAgreement,
                                     final boolean confirmed,
                                     final List<URI> targetList) throws PersistenceException {
        try {
            // Iterate of all targets to add the corresponding artifacts to the agreement.
            final var artifactList = new ArrayList<Artifact>();
            for (final var target : targetList) {
                final var uuid = EndpointUtils.getUUIDFromPath(target);
                final var artifact = artifactService.get(uuid);
                artifactList.add(artifact);
            }

            final var rdf = IdsUtils.toRdf(contractAgreement);

            final var desc = new AgreementDesc();
            desc.setConfirmed(confirmed);
            desc.setValue(rdf);
            desc.setArtifacts(artifactList);

            // Save agreement to return its id.
            final var agreement = agreementService.create(desc);
            return EndpointUtils.getSelfLink(agreement);
        } catch (Exception e) {
            LOGGER.warn("Could not store contract agreement. [exception=({})]", e.getMessage());
            throw new PersistenceException("Could not store contract agreement.", e);
        }
    }

    public boolean compareRulesOfOfferToRequest(final List<ContractRule> offerRules,
                                                final List<Rule> requestRules) {
        final var idsRuleList = new ArrayList<Rule>();
        for (final var rule : offerRules) {
            final var value = rule.getValue();
            final var idsRule = deserializationService.getRule(value);
            idsRuleList.add(idsRule);
        }

        try { // TODO What about duties?
            PolicyUtils.compareRules(idsRuleList, (ArrayList<Rule>) requestRules);
        } catch (ContractException exception) {
            LOGGER.debug("Rules do not match. [exception=({}), offer=({}), request=({})]",
                    exception.getMessage(), idsRuleList, requestRules);
            return false;
        }
        return true;
    }
}
