package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a contract.
 */
@Component
public class ContractFactory implements AbstractFactory<Contract, ContractDesc> {

    /**
     * Default remote id assigned to all contracts.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * Default consumer assigned to all contracts.
     */
    public static final URI DEFAULT_CONSUMER = URI.create("");

    /**
     * Default provider assigned to all contracts.
     */
    public static final URI DEFAULT_PROVIDER = URI.create("");

    /**
     * Default title assigned to all contracts.
     */
    public static final String DEFAULT_TITLE = "";

    /**
     * Default constructor.
     */
    public ContractFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new contract.
     * @param desc The description of the new contract.
     * @return The new contract.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    public Contract create(final ContractDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var contract = new Contract();
        contract.setRules(new ArrayList<>());
        contract.setResources(new ArrayList<>());

        update(contract, desc);

        return contract;
    }

    /**
     * Update a contract.
     * @param contract The contract to be updated.
     * @param desc     The new contract description.
     * @return True if the contract has been modified.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Override
    public boolean update(final Contract contract, final ContractDesc desc) {
        Utils.requireNonNull(contract, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedRemoteId = this.updateRemoteId(contract, desc.getRemoteId());
        final var hasUpdatedConsumer = this.updateConsumer(contract, desc.getConsumer());
        final var hasUpdatedProvider = this.updateProvider(contract, desc.getProvider());
        final var hasUpdatedTitle = this.updateTitle(contract, desc.getTitle());
        final var hasUpdatedAdditional = this.updateAdditional(contract, desc.getAdditional());

        final var hasUpdatedTime = this.updateTime(contract, contract.getStart(), desc.getEnd());

        return hasUpdatedRemoteId || hasUpdatedConsumer || hasUpdatedProvider || hasUpdatedTitle
               || hasUpdatedTime || hasUpdatedAdditional;
    }

    private boolean updateRemoteId(final Contract contract, final URI remoteId) {
        final var newUri =
                MetadataUtils.updateUri(contract.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(contract::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateConsumer(final Contract contract, final URI consumer) {
        final var newUri =
                MetadataUtils.updateUri(contract.getConsumer(), consumer, DEFAULT_CONSUMER);
        newUri.ifPresent(contract::setConsumer);

        return newUri.isPresent();
    }

    private boolean updateProvider(final Contract contract, final URI provider) {
        final var newUri =
                MetadataUtils.updateUri(contract.getProvider(), provider, DEFAULT_PROVIDER);
        newUri.ifPresent(contract::setProvider);

        return newUri.isPresent();
    }

    private boolean updateTitle(final Contract contract, final String title) {
        final var newTitle = MetadataUtils.updateString(contract.getTitle(), title, DEFAULT_TITLE);
        newTitle.ifPresent(contract::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateTime(final Contract contract, final ZonedDateTime start,
                               final ZonedDateTime end) {
        final var defaultTime = ZonedDateTime.now(ZoneOffset.UTC);
        final var newStart = MetadataUtils.updateDate(contract.getStart(), start, defaultTime);
        final var newEnd = MetadataUtils.updateDate(contract.getEnd(), end, defaultTime);

        // Validate the state of the contract with the new times
        var realStart = newStart.orElseGet(contract::getStart);
        var realEnd = newEnd.orElseGet(contract::getEnd);

        if (realStart.isAfter(realEnd)) {
            // Invalid state, fix up
            realStart = realEnd;
        }

        // Reiterate the operation
        final var finalStartValue =
                MetadataUtils.updateDate(contract.getStart(), realStart, defaultTime);
        final var finalEndValue = MetadataUtils.updateDate(contract.getEnd(), realEnd, defaultTime);

        finalStartValue.ifPresent(contract::setStart);
        finalEndValue.ifPresent(contract::setEnd);

        return finalStartValue.isPresent() || finalEndValue.isPresent();
    }

    private boolean updateAdditional(
            final Contract contract, final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                contract.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(contract::setAdditional);

        return newAdditional.isPresent();
    }

}
