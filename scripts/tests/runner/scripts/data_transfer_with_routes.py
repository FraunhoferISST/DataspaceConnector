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
import pprint
import sys

provider_url = "http://provider-dataspace-connector"
consumer_url = "http://consumer-dataspace-connector"
backend_url = "http://flask-route-backend:5000"


def main(argv):
    if len(argv) == 2:
        provider_url = argv[0]
        consumer_url = argv[1]
        print("Setting provider alias as:", provider_url)
        print("Setting consumer alias as:", consumer_url)


if __name__ == "__main__":
    main(sys.argv[1:])

print("Starting script")

# Provider
provider = ResourceApi(provider_url)

## Create resources
catalog = provider.create_catalog()
offers = provider.create_offered_resource()
representation = provider.create_representation()
contract = provider.create_contract()
use_rule = provider.create_rule()

endpoint = provider.create_endpoint(
    data={"location": backend_url + "/get", "type": "GENERIC"}
)
route = provider.create_route()
provider.add_start_endpoint_to_route(route, endpoint)
artifact = provider.create_artifact(data={"accessUrl": route})

provider.add_resource_to_catalog(catalog, offers)
provider.add_representation_to_resource(offers, representation)
provider.add_artifact_to_representation(representation, artifact)
provider.add_contract_to_resource(offers, contract)
provider.add_rule_to_contract(contract, use_rule)

print("Created provider resources")

# Consumer
consumer_resources = ResourceApi(consumer_url)

# Create route for dispatching data
consumer_endpoint = consumer_resources.create_endpoint(
    data={"location": backend_url + "/post", "type": "GENERIC"}
)
consumer_route = consumer_resources.create_route()
consumer_resources.add_end_endpoint_to_route(consumer_route, consumer_endpoint)

print("Created consumer route")

consumer = IdsApi(consumer_url)

# IDS
# Call description
offer = consumer.descriptionRequest(provider_url + "/api/ids/data", offers)

# Negotiate contract
obj = offer["ids:contractOffer"][0]["ids:permission"][0]
obj["ids:target"] = artifact
response = consumer.contractRequest(
    provider_url + "/api/ids/data", offers, artifact, False, obj
)
pprint.pprint(response)

# Pull data
agreement = response["_links"]["self"]["href"]

artifacts = consumer_resources.get_artifacts_for_agreement(agreement)
pprint.pprint(artifacts)

first_artifact = artifacts["_embedded"]["artifacts"][0]["_links"]["self"]["href"]
pprint.pprint(first_artifact)

data = consumer_resources.get_data_with_route(first_artifact, consumer_route).text
pprint.pprint(data)

if data != "data string":
    print("Did not receive expected data.")
    exit(1)
else:
    print("Success!")

exit(0)
