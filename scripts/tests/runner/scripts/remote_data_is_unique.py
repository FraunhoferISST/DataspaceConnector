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

import requests
import pprint
import json

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()

s = requests.Session()
s.auth = ("admin", "password")
s.verify = False

provider_url = "http://provider-dataspace-connector"

def create_catalog():
    response = s.post(provider_url + "/api/catalogs", json={})
    return response.headers["Location"]


def create_offered_resource():
    response = s.post(provider_url + "/api/offers", json={})
    return response.headers["Location"]


def create_representation():
    response = s.post(provider_url + "/api/representations", json={})
    return response.headers["Location"]


def create_artifact():
    response = s.post(
        provider_url + "/api/artifacts", json={"value": "SOME LONG VALUE"}
    )
    return response.headers["Location"]


def create_contract():
    response = s.post(
        provider_url + "/api/contracts",
        json={
            "start": "2021-04-06T13:33:44.995+02:00",
            "end": "2022-12-06T13:33:44.995+02:00",
        },
        )
    return response.headers["Location"]


def create_rule_allow_access():
    response = s.post(
        provider_url + "/api/rules",
        json={
            "value": """{
        "@context" : {
            "ids" : "https://w3id.org/idsa/core/",
            "idsc" : "https://w3id.org/idsa/code/"
        },
        "@type": "ids:Permission",
        "@id": "https://w3id.org/idsa/autogen/permission/cf1cb758-b96d-4486-b0a7-f3ac0e289588",
        "ids:action": [
            {
            "@id": "idsc:USE"
            }
        ],
        "ids:description": [
            {
            "@value": "provide-access",
            "@type": "http://www.w3.org/2001/XMLSchema#string"
            }
        ],
        "ids:title": [
            {
            "@value": "Example Usage Policy",
            "@type": "http://www.w3.org/2001/XMLSchema#string"
            }
        ]
        }"""
        },
        )
    return response.headers["Location"]


def add_resource_to_catalog(catalog, resource):
    response = s.post(catalog + "/offers", json=[resource])


def add_catalog_to_resource(resource, catalog):
    response = s.post(resource + "/catalogs", json=[catalog])


def add_representation_to_resource(resource, representation):
    response = s.post(resource + "/representations", json=[representation])


def add_artifact_to_representation(representation, artifact):
    response = s.post(representation + "/artifacts", json=[artifact])


def add_contract_to_resource(resource, contract):
    response = s.post(resource + "/contracts", json=[contract])


def add_rule_to_contract(contract, rule):
    response = s.post(contract + "/rules", json=[rule])


# IDS
def descriptionRequest(recipient, elementId):
    url = provider_url + "/api/ids/description"
    params = {}
    if recipient is not None:
        params["recipient"] = recipient
    if elementId is not None:
        params["elementId"] = elementId

    return s.post(url, params=params)


def contractRequest(recipient, resourceId, artifactId, download, contract):
    url = provider_url + "/api/ids/contract"
    params = {}
    if recipient is not None:
        params["recipient"] = recipient
    if resourceId is not None:
        params["resourceIds"] = resourceId
    if artifactId is not None:
        params["artifactIds"] = artifactId
    if download is not None:
        params["download"] = download

    return s.post(url, params=params, json=[contract])


# Create resources
catalog = create_catalog()
offers = create_offered_resource()
anotherOffers = create_offered_resource()
representation = create_representation()
artifact = create_artifact()
contract = create_contract()
use_rule = create_rule_allow_access()

# Link resources
add_resource_to_catalog(catalog, offers)
add_representation_to_resource(offers, representation)
add_representation_to_resource(anotherOffers, representation)
add_artifact_to_representation(representation, artifact)
add_contract_to_resource(offers, contract)
add_contract_to_resource(anotherOffers, contract)
add_rule_to_contract(contract, use_rule)

# Call description
response = descriptionRequest(provider_url + "/api/ids/data", offers)
offer = json.loads(response.text)

# Negotiate contract
obj = offer["ids:contractOffer"][0]["ids:permission"][0]
obj["ids:target"] = artifact
response = contractRequest(
    provider_url + "/api/ids/data", offers, artifact, False, obj
)
pprint.pprint(str(response.content))

# Collect stats
numReqResources = json.loads(s.get(provider_url + "/api/requests").text)["page"][
    "totalElements"
]
numRepresentations = json.loads(
    s.get(provider_url + "/api/representations").text
)["page"]["totalElements"]
numArtifacts = json.loads(s.get(provider_url + "/api/artifacts").text)["page"][
    "totalElements"
]
numAgreements = json.loads(s.get(provider_url + "/api/agreements").text)["page"][
    "totalElements"
]

# Negotiate over resource whose representations and artifacts are exactly the same
response = descriptionRequest(provider_url + "/api/ids/data", anotherOffers)
offer = json.loads(response.text)

obj = offer["ids:contractOffer"][0]["ids:permission"][0]
obj["ids:target"] = artifact
response = contractRequest(
    provider_url + "/api/ids/data", anotherOffers, artifact, False, obj
)
pprint.pprint(str(response.content))

# Make sure only 2 resources exists all the rest is the same
numReqResourcesAfter = json.loads(s.get(provider_url + "/api/requests").text)[
    "page"
]["totalElements"]
numRepresentationsAfter = json.loads(
    s.get(provider_url + "/api/representations").text
)["page"]["totalElements"]
numArtifactsAfter = json.loads(s.get(provider_url + "/api/artifacts").text)[
    "page"
]["totalElements"]
numAgreementsAfter = json.loads(s.get(provider_url + "/api/agreements").text)[
    "page"
]["totalElements"]

if numReqResources + 1 != numReqResourcesAfter:
    raise Exception("Wrong number of requested resources.")

if numRepresentations != numRepresentationsAfter:
    raise Exception("Wrong number of representations")

if numArtifacts != numArtifactsAfter:
    raise Exception("Wrong number of artifacts")

if numAgreements + 2 != numAgreementsAfter:  # +1 as consumer + 1 as provider
    raise Exception("Wrong number of agreements")
