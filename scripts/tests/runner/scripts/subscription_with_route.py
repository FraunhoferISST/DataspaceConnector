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
from subscriptionapi import SubscriptionApi
import pprint
import sys
import requests

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
dataValue = "SOME LONG VALUE"
catalog = provider.create_catalog()
offers = provider.create_offered_resource()
representation = provider.create_representation()
artifact = provider.create_artifact(data={"value": dataValue})
contract = provider.create_contract()
use_rule = provider.create_rule()

## Link resources
provider.add_resource_to_catalog(catalog, offers)
provider.add_representation_to_resource(offers, representation)
provider.add_artifact_to_representation(representation, artifact)
provider.add_contract_to_resource(offers, contract)
provider.add_rule_to_contract(contract, use_rule)

print("Created provider resources")

# Consumer
consumer = IdsApi(consumer_url)
consumer_resources = ResourceApi(consumer_url)

# IDS
# Call description
offer = consumer.descriptionRequest(provider_url + "/api/ids/data", offers)

# Negotiate contract
obj = offer["ids:contractOffer"][0]["ids:permission"][0]
obj["ids:target"] = artifact
response = consumer.contractRequest(
    provider_url + "/api/ids/data", offers, artifact, True, obj
)
pprint.pprint(response)

# Pull data
agreement = response["_links"]["self"]["href"]

artifacts = consumer_resources.get_artifacts_for_agreement(agreement)
pprint.pprint(artifacts)

first_artifact = artifacts["_embedded"]["artifacts"][0]["_links"]["self"]["href"]
pprint.pprint(first_artifact)

# Create route
consumer_endpoint = consumer_resources.create_endpoint(
    data={"location": backend_url + "/subscription", "type": "GENERIC"}
)
consumer_route = consumer_resources.create_route()
consumer_resources.add_end_endpoint_to_route(consumer_route, consumer_endpoint)

print("Created consumer route")

# Create IDS subscription
consumer_subscriptions = SubscriptionApi(consumer_url)
consumer_subscriptions.subscription_message(
    data={
        "title": "IDS subscription",
        "target": artifact,
        "location": consumer_url + "/api/ids/data",
        "subscriber": consumer_url,
        "pushData": True,
    },
    params={"recipient": provider_url + "/api/ids/data"},
)

# Create backend subscription
consumer_subscriptions.create_subscription(
    data={
        "title": "Backend subscription",
        "target": first_artifact,
        "location": consumer_route,
        "subscriber": backend_url,
        "pushData": True,
    }
)

print("Created subscriptions")

# Update data on provider side
provider.set_data(artifact, "data string")

# Verify that update has been received in backend
update_received = requests.Session().get(backend_url + "/subscription").text

if update_received == "true":
    print("Subscription successfully received.")
else:
    print("Subscription not received.")
    exit(1)

exit(0)
