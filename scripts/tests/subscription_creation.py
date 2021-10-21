#!/usr/bin/env python3
#
# Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
import requests
import sys

from resourceapi import ResourceApi
from subscriptionapi import SubscriptionApi

providerUrl = "http://localhost:8080"
consumerUrl = "http://localhost:8081"

provider_alias = "http://provider-dataspace-connector"
consumer_alias = "http://consumer-dataspace-connector"


def main(argv):
    if len(argv) == 2:
        provider_alias = argv[0]
        consumer_alias = argv[1]
        print("Setting provider alias as:", provider_alias)
        print("Setting consumer alias as:", consumer_alias)


if __name__ == "__main__":
    main(sys.argv[1:])

print("Starting script")

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()

# Provider
provider = ResourceApi(providerUrl)
providerSub = SubscriptionApi(providerUrl)

# Consumer
consumerSub = SubscriptionApi(consumerUrl)

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
    "location": provider_alias+"/api/ids/data",
    "subscriber": consumer_alias,
    "pushData": "true"
}

response = consumerSub.subscription_message(data=data,
                                            params={'recipient': provider_alias + '/api/ids/data'})

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
