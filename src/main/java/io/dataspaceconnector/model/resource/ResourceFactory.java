/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.model.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.util.FactoryUtils;

/**
 * Base class for creating and updating resources.
 *
 * @param <T> The resource type.
 * @param <D> The description type.
 */
public abstract class ResourceFactory<T extends Resource, D extends ResourceDesc>
        extends AbstractNamedFactory<T, D> {

    /**
     * The default keywords assigned to all resources.
     */
    public static final List<String> DEFAULT_KEYWORDS = List.of("DSC");

    /**
     * The default publisher assigned to all resources.
     */
    public static final URI DEFAULT_PUBLISHER = URI.create("");

    /**
     * The default language assigned to all resources.
     */
    public static final String DEFAULT_LANGUAGE = "";

    /**
     * The default license assigned to all resources.
     */
    public static final URI DEFAULT_LICENSE = URI.create("");

    /**
     * The default sovereign assigned to all resources.
     */
    public static final URI DEFAULT_SOVEREIGN = URI.create("");

    /**
     * The default endpoint documentation assigned to all resources.
     */
    public static final URI DEFAULT_ENDPOINT_DOCS = URI.create("");

    /**
     * The default payment modality assigned to all resources.
     */
    public static final PaymentMethod DEFAULT_PAYMENT_MODALITY = PaymentMethod.UNDEFINED;

    /**
     * The default sample list assigned to all resources.
     */
    public static final URI[] DEFAULT_SAMPLES = {};

    /**
     * Create a new resource.
     *
     * @param desc The description of the new resource.
     * @return The new resource.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    protected T initializeEntity(final D desc) {
        final var resource = createInternal(desc);
        resource.setRepresentations(new ArrayList<>());
        resource.setContracts(new ArrayList<>());
        resource.setCatalogs(new ArrayList<>());
        resource.setSubscriptions(new ArrayList<>());

        update(resource, desc);

        return resource;
    }

    /**
     * Create a new resource. Implement type specific stuff here.
     *
     * @param desc The description passed to the factory.
     * @return The new resource.
     */
    protected abstract T createInternal(D desc);

    /**
     * Update a resource.
     *
     * @param resource The resource to be updated.
     * @param desc     The new resource description.
     * @return True if the resource has been modified.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Override
    public boolean update(final T resource, final D desc) {
        final var hasParentUpdated = super.update(resource, desc);
        final var hasUpdatedKeywords = updateKeywords(resource, desc.getKeywords());
        final var hasUpdatedPublisher = updatePublisher(resource, desc.getPublisher());
        final var hasUpdatedLanguage = updateLanguage(resource, desc.getLanguage());
        final var hasUpdatedLicense = updateLicense(resource, desc.getLicense());
        final var hasUpdatedSovereign = updateSovereign(resource, desc.getSovereign());
        final var hasUpdatedEndpointDocs =
                updateEndpointDocs(resource, desc.getEndpointDocumentation());
        final var hasUpdatedPaymentMethod = updatePaymentMethod(resource, desc.getPaymentMethod());
        final var hasUpdatedSamples = updateSamples(resource, desc.getSamples());
        final var hasChildUpdated = updateInternal(resource, desc);

        final var hasUpdated = hasParentUpdated || hasChildUpdated
                || hasUpdatedKeywords || hasUpdatedPublisher
                || hasUpdatedLanguage || hasUpdatedLicense
                || hasUpdatedSovereign || hasUpdatedEndpointDocs
                || hasUpdatedPaymentMethod || hasUpdatedSamples;

        if (hasUpdated) {
            resource.setVersion(resource.getVersion() + 1);
        }

        return hasUpdated;
    }

    /**
     * Update a resource's keywords.
     *
     * @param resource The resource.
     * @param keywords The new keywords.
     * @return true if the resource's keywords have been modified.
     */
    protected final boolean updateKeywords(final Resource resource, final List<String> keywords) {
        final var newKeys =
                FactoryUtils.updateStringList(resource.getKeywords(), keywords, DEFAULT_KEYWORDS);
        newKeys.ifPresent(resource::setKeywords);

        return newKeys.isPresent();
    }

    /**
     * Update a resource's publisher.
     *
     * @param resource  The resource.
     * @param publisher The new publisher.
     * @return true if the resource's publisher has been modified.
     */
    protected final boolean updatePublisher(final Resource resource, final URI publisher) {
        final var newPublisher =
                FactoryUtils.updateUri(resource.getPublisher(), publisher, DEFAULT_PUBLISHER);
        newPublisher.ifPresent(resource::setPublisher);

        return newPublisher.isPresent();
    }

    /**
     * Update a resource's language.
     *
     * @param resource The resource.
     * @param language The new language.
     * @return true if the resource's language has been modified.
     */
    protected final boolean updateLanguage(final Resource resource, final String language) {
        final var newLanguage =
                FactoryUtils.updateString(resource.getLanguage(), language, DEFAULT_LANGUAGE);
        newLanguage.ifPresent(resource::setLanguage);

        return newLanguage.isPresent();
    }

    /**
     * Update a resource's license.
     *
     * @param resource The resource.
     * @param license  The new license.
     * @return true if the resource's license has been modified.
     */
    protected final boolean updateLicense(final Resource resource, final URI license) {
        final var newLicense =
                FactoryUtils.updateUri(resource.getLicense(), license, DEFAULT_LICENSE);
        newLicense.ifPresent(resource::setLicense);

        return newLicense.isPresent();
    }

    /**
     * Update a resource's sovereign.
     *
     * @param resource  The resource.
     * @param sovereign The new sovereign.
     * @return true if the resource's sovereign has been modified.
     */
    protected final boolean updateSovereign(final Resource resource, final URI sovereign) {
        final var newPublisher =
                FactoryUtils.updateUri(resource.getSovereign(), sovereign, DEFAULT_SOVEREIGN);
        newPublisher.ifPresent(resource::setSovereign);

        return newPublisher.isPresent();
    }

    /**
     * Update a resource's endpoint documentation.
     *
     * @param resource     The resource.
     * @param endpointDocs The new endpoint documentation.
     * @return true if the resource's endpoint documentation has been modified.
     */
    protected final boolean updateEndpointDocs(final Resource resource, final URI endpointDocs) {
        final var newPublisher = FactoryUtils.updateUri(
                resource.getEndpointDocumentation(), endpointDocs, DEFAULT_ENDPOINT_DOCS);
        newPublisher.ifPresent(resource::setEndpointDocumentation);

        return newPublisher.isPresent();
    }

    /**
     * Update a resource's payment modality.
     *
     * @param resource      The resource.
     * @param paymentMethod The new payment modality.
     * @return true if the resource's payment modality has been modified.
     */
    protected final boolean updatePaymentMethod(final Resource resource,
                                                final PaymentMethod paymentMethod) {
        final var tmp = paymentMethod == null ? DEFAULT_PAYMENT_MODALITY : paymentMethod;
        if (tmp.equals(resource.getPaymentModality())) {
            return false;
        }

        resource.setPaymentModality(tmp);
        return true;
    }

    /**
     * Update a resource's samples.
     *
     * @param resource The resource.
     * @param samples  The new samples.
     * @return true if the resource's samples have been modified.
     */
    protected final boolean updateSamples(final Resource resource, final List<URI> samples) {
        if (samples != null) {
            validateSamples(resource, samples);
        }
        final var newList
                = FactoryUtils.updateUriList(resource.getSamples(), samples,
                                             Arrays.asList(DEFAULT_SAMPLES));
        newList.ifPresent(resource::setSamples);

        return newList.isPresent();
    }

    /**
     * Update a resource's samples. Implement type specific stuff here.
     *
     * @param resource The resource passed to the factory.
     * @param samples  The new samples.
     */
    protected abstract void validateSamples(Resource resource, List<URI> samples);
}
