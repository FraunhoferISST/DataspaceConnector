package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.ContractAgreement;
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
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.AgreementViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
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

    private final @NonNull IdsConnectorService connectorService;

    /**
     * Service for deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    private final @NonNull AgreementService agreementService;

    private final @NonNull AgreementViewAssembler viewAssembler;

    /**
     * Validate rule input and build contract request.
     *
     * @param ruleList The ids rule list.
     * @return The ids contract request.
     * @throws InvalidContractException     If the user input is invalid.
     * @throws ConstraintViolationException If the contract request could not be built.
     */
    public ContractRequest validateAndBuildContractRequest(final List<? extends Rule> ruleList)
            throws InvalidContractException, ConstraintViolationException {
        PolicyUtils.validateRuleTarget(ruleList);
        return buildContractRequest(ruleList);
    }

    /**
     * Read and validate ids contract agreement from ids response message.
     *
     * @param response        The response map containing ids header and payload.
     * @param contractRequest The contract request that was sent.
     * @return The ids contract agreement.
     * @throws MessageResponseException If the response could not be processed.
     * @throws IllegalArgumentException If deserialization fails.
     * @throws ContractException        If the contract's content is invalid.
     */
    public ContractAgreement readAndValidateAgreementFromResponse(final Map<String, String> response,
                                                                  final ContractRequest contractRequest)
            throws MessageResponseException, IllegalArgumentException, ContractException {
        final var payload = MessageUtils.extractPayloadFromMultipartMessage(response);
        return validateContractAgreement(contractRequest, payload);
    }

    /**
     * Get stored contract agreement for requested element.
     *
     * @param element The requested element.
     * @return The respective contract agreement.
     */
    public ContractAgreement getContractAgreementForRequestedElement(final URI element) {
        // TODO Get abstract entity by id (resource, artifact, catalog)
        return null;
    }

    /**
     * Iterate over all rules of a contract agreement and add the ones with the element as their
     * target to a rule list.
     *
     * @param agreement The contract agreement.
     * @param element   The requested element.
     * @return List of ids rules.
     */
    public List<? extends Rule> getRulesForRequestedElement(final ContractAgreement agreement,
                                                  final URI element) {
        List<Rule> rules = new ArrayList<>();

        for (Permission permission : agreement.getPermission()) {
            final var target = permission.getTarget();
            if (element == target) {
                rules.add(permission);
            }
        }

        for (Prohibition prohibition : agreement.getProhibition()) {
            final var target = prohibition.getTarget();
            if (element == target) {
                rules.add(prohibition);
            }
        }

        for (Duty obligation : agreement.getObligation()) {
            final var target = obligation.getTarget();
            if (element == target) {
                rules.add(obligation);
            }
        }

        return rules;
    }

    /**
     * Build contract request from a list of rules - with assignee and provider.
     *
     * @param ruleList The rule list.
     * @return The ids contract request.
     * @throws ConstraintViolationException If ids contract building fails.
     */
    private ContractRequest buildContractRequest(final List<? extends Rule> ruleList)
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
                ._contractDate_(IDSUtils.getGregorianNow())
                ._contractStart_(IDSUtils.getGregorianNow())
                ._obligation_(obligations)
                ._permission_(permissions)
                ._prohibition_(prohibitions)
                .build();
    }

    /**
     * Validate the content if the received contract agreement.
     *
     * @param request The sent request.
     * @param payload The message response's payload.
     * @return The ids contract agreement.
     * @throws IllegalArgumentException If deserialization fails.
     * @throws ContractException        If the content does not match the original request.
     */
    private ContractAgreement validateContractAgreement(final ContractRequest request,
                                                        final String payload) throws IllegalArgumentException, ContractException {
        final var agreement = deserializationService.deserializeContractAgreement(payload);

        PolicyUtils.validateRuleAssigner(agreement);
        PolicyUtils.validateRuleContent(request, agreement);

        return agreement;
    }

    /**
     * Save contract agreement to database.
     *
     * @param contractAgreement The ids contract agreement.
     * @return The id of the stored contract agreement.
     * @throws PersistenceException If the contract agreement could not be saved.
     */
    public URI saveContractAgreement(final ContractAgreement contractAgreement)
            throws PersistenceException {
        try {
            final var remoteId = contractAgreement.getId();
            final var rdf = IdsUtils.toRdf(contractAgreement);

            final var desc = new AgreementDesc();
            desc.setRemoteId(remoteId);
            desc.setValue(rdf);
            final var agreement = agreementService.create(desc);

            // Get id of the stored agreement.
            final var entity = viewAssembler.toModel(agreement);
            return entity.getLink("self").get().toUri();
        } catch (Exception e) {
            LOGGER.warn("Could not store contract agreement. [exception=({})]", e.getMessage());
            throw new PersistenceException("Could not store contract agreement.", e);
        }
    }

    public URI getOriginalContractAgreementId(final URI id) {
        final var endpoint = EndpointUtils.getEndpointIdFromPath(id);
        final var uuid = endpoint.getResourceId();
        final var agreement = agreementService.get(uuid);
        // agreement.getOriginalId(); TODO
        return null;
    }
}
