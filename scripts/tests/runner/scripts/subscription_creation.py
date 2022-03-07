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

import pprint
import sys

from resourceapi import ResourceApi
from subscriptionapi import SubscriptionApi

provider_url = "http://provider-dataspace-connector"
consumer_url = "http://consumer-dataspace-connector"


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
providerSub = SubscriptionApi(provider_url)

# Consumer
consumerSub = SubscriptionApi(consumer_url)

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

data = {
    "title": "string",
    "description": "string",
    "target": offers,
    "location": provider_url + "/api/ids/data",
    "subscriber": consumer_url,
    "pushData": "true",
}

response = consumerSub.subscription_message(
    data=data, params={"recipient": provider_url + "/api/ids/data"}
)

pprint.pprint(response.text)
if response.status_code != 200:
    exit(1)


check = providerSub.get_subscriptions()
pprint.pprint(check.text)
if check.status_code != 200:
    exit(1)

if not check.text.__contains__(offers):
    exit(1)

exit(0)
