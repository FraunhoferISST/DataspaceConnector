#!/usr/bin/env python3
#
# Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from resourceapi import ResourceApi
from idsapi import IdsApi
import requests
import pprint
import sys

providerUrl = "http://localhost:8080"
consumerUrl = "http://localhost:8081"

provider_alias = "http://provider-dataspace-connector"
consumer_alias = "http://consumer-dataspace-connector"

print("Starting script")

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()

# Setup consumer APIs
consumerResources = ResourceApi(consumerUrl)
consumer = IdsApi(consumerUrl)

# Expected data value
dataValue = "SOME LONG VALUE"

# Request data from provider with a previously created agreement
requestedArtifact = consumerResources.get_requested_artifact()
data = consumerResources.get_data_force_download(requestedArtifact).text
pprint.pprint(data)

if data != dataValue:
    exit(1)

# Negotiate a new contract with the provider
# Get resource metadata
selfDescription = consumer.descriptionRequest(provider_alias + "/api/ids/data", None)
catalogId = selfDescription["ids:resourceCatalog"][0]["@id"]
catalog = consumer.descriptionRequest(provider_alias + "/api/ids/data", catalogId)
offerId = catalog["ids:offeredResource"][0]["@id"]
offer = consumer.descriptionRequest(provider_alias + "/api/ids/data", offerId)
artifactId = offer["ids:representation"][0]["ids:instance"][0]["@id"]
pprint.pprint(offer)

# Negotiate contract
obj = offer["ids:contractOffer"][0]["ids:permission"][0]
obj["ids:target"] = artifactId
response = consumer.contractRequest(
    provider_alias + "/api/ids/data", offerId, artifactId, False, obj
)
pprint.pprint(response)

# Request data
agreement = response["_links"]["self"]["href"]
artifacts = consumerResources.get_artifacts_for_agreement(agreement)
pprint.pprint(artifacts)

first_artifact = artifacts["_embedded"]["artifacts"][0]["_links"]["self"]["href"]
pprint.pprint(first_artifact)

data = consumerResources.get_data(first_artifact).text
pprint.pprint(data)

if data != dataValue:
    exit(1)

exit(0)
