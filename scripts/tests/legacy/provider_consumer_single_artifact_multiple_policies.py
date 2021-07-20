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

import requests
import pprint
import json

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()

s = requests.Session()
s.auth = ("admin", "password")
s.verify = False

####################################################################################################
# PROVIDER (running on port 8080)                                                                  #
####################################################################################################


def create_catalog():
    return s.post("http://localhost:8080/api/catalogs", json={}).headers["Location"]


def create_offered_resource():
    return s.post("http://localhost:8080/api/offers", json={}).headers["Location"]


def create_representation():
    return s.post("http://localhost:8080/api/representations", json={}).headers[
        "Location"
    ]


def create_artifact():
    return s.post(
        "http://localhost:8080/api/artifacts", json={"value": "SOME LONG VALUE"}
    ).headers["Location"]


def create_contract():
    return s.post("http://localhost:8080/api/contracts", json={}).headers["Location"]


def create_usage_notification_rule():
    return s.post(
        "http://localhost:8080/api/rules",
        json={
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
        },
    ).headers["Location"]


def create_n_times_usage_rule():
    return s.post(
        "http://localhost:8080/api/rules",
        json={
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
        },
    ).headers["Location"]


def add_resource_to_catalog(catalog, resource):
    s.post(catalog + "/offers", json=[resource])


def add_catalog_to_resource(resource, catalog):
    s.post(resource + "/catalogs", json=[catalog])


def add_representation_to_resource(resource, representation):
    s.post(resource + "/representations", json=[representation])


def add_artifact_to_representation(representation, artifact):
    s.post(representation + "/artifacts", json=[artifact])


def add_contract_to_resource(resource, contract):
    s.post(resource + "/contracts", json=[contract])


def add_rule_to_contract(contract, rule):
    s.post(contract + "/rules", json=[rule])


# for i in tqdm.tqdm(range(500)):
catalog = create_catalog()
offers = create_offered_resource()
representation = create_representation()
artifact = create_artifact()
contract = create_contract()
notification_rule = create_usage_notification_rule()
count_rule = create_n_times_usage_rule()


add_resource_to_catalog(catalog, offers)
add_representation_to_resource(offers, representation)
add_artifact_to_representation(representation, artifact)
add_contract_to_resource(offers, contract)
add_rule_to_contract(contract, notification_rule)
add_rule_to_contract(contract, count_rule)

####################################################################################################
# CONSUMER (running on port 8080)                                                                  #
####################################################################################################

provider = "http://localhost:8080/api/ids/data"


def descriptionRequest(recipient, elementId):
    params = {}
    if recipient is not None:
        params["recipient"] = recipient
    if elementId is not None:
        params["elementId"] = elementId

    return s.post("http://localhost:8080/api/ids/description", params=params)


def contractRequest(recipient, resourceId, artifactId, download, contract):
    params = {}
    if recipient is not None:
        params["recipient"] = recipient
    if resourceId is not None:
        params["resourceIds"] = resourceId
    if artifactId is not None:
        params["artifactIds"] = artifactId
    if download is not None:
        params["download"] = download

    return s.post(
        "http://localhost:8080/api/ids/contract", params=params, json=contract
    )


response = descriptionRequest(provider, catalog)
catalogResponse = json.loads(response.text)

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

response = descriptionRequest(provider, contractId)
contractResponse = json.loads(response.text)

notify = contractResponse["ids:permission"][0]
notify["ids:target"] = artifactId

count = contractResponse["ids:permission"][1]
count["ids:target"] = artifactId

# Only accept one rule -> causes a ContractRejectionMessage
# response = contractRequest(provider, resourceId, artifactId, True, notify)
# pprint.pprint(str(response.content))

# Accept both rules
body = [notify, count]
response = contractRequest(provider, resourceId, artifactId, True, body)
pprint.pprint(str(response.content))
