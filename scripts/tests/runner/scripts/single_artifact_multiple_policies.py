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

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()

# Provider
provider = ResourceApi(provider_url)

## Create resources
catalog = provider.create_catalog()
offers = provider.create_offered_resource()
representation = provider.create_representation()
artifact = provider.create_artifact()
contract = provider.create_contract()
notification_rule = provider.create_rule(
    data={
        "value": """{
        "@context" : {
            "ids" : "http://w3id.org/idsa/core/",
            "idsc" : "http://w3id.org/idsa/code/"
        },
      "@type": "ids:Permission",
      "@id": "http://w3id.org/idsa/autogen/permission/c0bdb9d5-e86a-4bb3-86d2-2b1dc9d226f5",
      "ids:description": [
        {
          "@value": "usage-notification",
          "@type": "http://www.w3.org/2001/XMLSchema#string"
        }
      ],
      "ids:title": [
        {
          "@value": "Example Usage Policy",
          "@type": "http://www.w3.org/2001/XMLSchema#string"
        }
      ],
      "ids:action": [
        {
          "@id": "idsc:USE"
        }
      ],
      "ids:postDuty": [
        {
          "@type": "ids:Duty",
          "@id": "http://w3id.org/idsa/autogen/duty/863d2fac-1072-476d-b504-9d6347fe4b6f",
          "ids:action": [
            {
              "@id": "idsc:NOTIFY"
            }
          ],
          "ids:constraint": [
            {
              "@type": "ids:Constraint",
              "@id": "http://w3id.org/idsa/autogen/constraint/c91e64ce-1fc1-44fd-bec1-6c6778603919",
              "ids:rightOperand": {
                "@value": "http://localhost:8080/api/ids/data",
                "@type": "http://www.w3.org/2001/XMLSchema#anyURI"
              },
              "ids:leftOperand": {
                "@id": "idsc:ENDPOINT"
              },
              "ids:operator": {
                "@id": "idsc:DEFINES_AS"
              }
            }
          ]
        }
      ]
    }"""
    }
)
count_rule = provider.create_rule(
    data={
        "value": """{
        "@context" : {
            "ids" : "http://w3id.org/idsa/core/",
            "idsc" : "http://w3id.org/idsa/code/"
        },
      "@type": "ids:Permission",
      "@id": "http://w3id.org/idsa/autogen/permission/154df1cf-557b-4f44-b839-4b68056606a2",
      "ids:description": [
        {
          "@value": "n-times-usage",
          "@type": "http://www.w3.org/2001/XMLSchema#string"
        }
      ],
      "ids:title": [
        {
          "@value": "Example Usage Policy",
          "@type": "http://www.w3.org/2001/XMLSchema#string"
        }
      ],
      "ids:action": [
        {
          "@id": "idsc:USE"
        }
      ],
      "ids:constraint": [
        {
          "@type": "ids:Constraint",
          "@id": "http://w3id.org/idsa/autogen/constraint/4ae656d1-2a73-44e3-a168-b1cbe49d4622",
          "ids:rightOperand": {
            "@value": "5",
            "@type": "http://www.w3.org/2001/XMLSchema#double"
          },
          "ids:leftOperand": {
            "@id": "idsc:COUNT"
          },
          "ids:operator": {
            "@id": "idsc:LTEQ"
          }
        }
      ]
    }"""
    }
)

## Link Resources
provider.add_resource_to_catalog(catalog, offers)
provider.add_representation_to_resource(offers, representation)
provider.add_artifact_to_representation(representation, artifact)
provider.add_contract_to_resource(offers, contract)
provider.add_rule_to_contract(contract, notification_rule)
provider.add_rule_to_contract(contract, count_rule)

print("Created provider resources")

# Consumer
consumer = IdsApi(consumer_url)

##
catalogResponse = consumer.descriptionRequest(provider_url + "/api/ids/data", catalog)
obj = catalogResponse["ids:offeredResource"][0]
resourceId = obj["@id"]
pprint.pprint(resourceId)
contract = obj["ids:contractOffer"][0]
contractId = contract["@id"]
pprint.pprint(contractId)
representation = obj["ids:representation"][0]
artifact = representation["ids:instance"][0]
artifactId = artifact["@id"]
pprint.pprint(artifactId)

##
contractResponse = consumer.descriptionRequest(
    provider_url + "/api/ids/data", contractId
)
notify = contractResponse["ids:permission"][0]
notify["ids:target"] = artifactId

count = contractResponse["ids:permission"][1]
count["ids:target"] = artifactId

# Accept both rules
body = [notify, count]
response = consumer.contractRequest(
    provider_url + "/api/ids/data", resourceId, artifactId, True, body
)
pprint.pprint(response)

exit(0)
