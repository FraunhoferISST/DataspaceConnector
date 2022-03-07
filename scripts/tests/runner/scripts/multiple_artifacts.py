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
offers2 = provider.create_offered_resource()
representation = provider.create_representation()
representation2 = provider.create_representation()
local_artifact = provider.create_artifact(data={"value": dataValue})
remote_artifact = provider.create_artifact(data={"accessUrl": "https://www.google.de/"})
contract = provider.create_contract()
contract2 = provider.create_contract()
notification_rule = provider.create_rule(
    data={
        "value": """{
        "@context" : {
            "ids" : "https://w3id.org/idsa/core/",
            "idsc" : "https://w3id.org/idsa/code/"
        },
      "@type": "ids:Permission",
      "@id": "https://w3id.org/idsa/autogen/permission/c0bdb9d5-e86a-4bb3-86d2-2b1dc9d226f5",
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
          "@id": "https://w3id.org/idsa/autogen/duty/863d2fac-1072-476d-b504-9d6347fe4b6f",
          "ids:action": [
            {
              "@id": "idsc:NOTIFY"
            }
          ],
          "ids:constraint": [
            {
              "@type": "ids:Constraint",
              "@id": "https://w3id.org/idsa/autogen/constraint/c91e64ce-1fc1-44fd-bec1-6c6778603919",
              "ids:rightOperand": {
                "@value": "https://localhost:8080/api/ids/data",
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

n_time_usage_rule = provider.create_rule(
    data={
        "value": """{
        "@context" : {
            "ids" : "https://w3id.org/idsa/core/",
            "idsc" : "https://w3id.org/idsa/code/"
        },
      "@type": "ids:Permission",
      "@id": "https://w3id.org/idsa/autogen/permission/154df1cf-557b-4f44-b839-4b68056606a2",
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
          "@id": "https://w3id.org/idsa/autogen/constraint/4ae656d1-2a73-44e3-a168-b1cbe49d4622",
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
provider.add_artifact_to_representation(representation, local_artifact)
provider.add_contract_to_resource(offers, contract)
provider.add_rule_to_contract(contract, notification_rule)

provider.add_resource_to_catalog(catalog, offers2)
provider.add_representation_to_resource(offers2, representation2)
provider.add_artifact_to_representation(representation2, remote_artifact)
provider.add_contract_to_resource(offers2, contract2)
provider.add_rule_to_contract(contract2, n_time_usage_rule)

print("Created provider resources")

# Consumer
consumer = IdsApi(consumer_url)

##
catalogResponse = consumer.descriptionRequest(provider_url + "/api/ids/data", catalog)
obj = catalogResponse["ids:offeredResource"][0]
resourceId1 = obj["@id"]
contract = obj["ids:contractOffer"][0]
contractId = contract["@id"]
representation = obj["ids:representation"][0]
artifact = representation["ids:instance"][0]
artifactId1 = artifact["@id"]

##
contract1Response = consumer.descriptionRequest(
    provider_url + "/api/ids/data", contractId
)

obj = catalogResponse["ids:offeredResource"][1]
resourceId2 = obj["@id"]
contract = obj["ids:contractOffer"][0]
contractId = contract["@id"]
representation = obj["ids:representation"][0]
artifact = representation["ids:instance"][0]
artifactId2 = artifact["@id"]

##
contract2Response = consumer.descriptionRequest(
    provider_url + "/api/ids/data", contractId
)

##
notify = contract1Response["ids:permission"][0]
notify["ids:target"] = artifactId1

##
count = contract2Response["ids:permission"][0]
count["ids:target"] = artifactId2

##
body = [notify, count]
resources = [resourceId1, resourceId2]
artifacts = [artifactId1, artifactId2]
response = consumer.contractRequest(
    provider_url + "/api/ids/data", resources, artifacts, True, body
)
pprint.pprint(response)
