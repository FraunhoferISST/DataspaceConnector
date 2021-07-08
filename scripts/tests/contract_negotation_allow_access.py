#!/usr/bin/env python3

import requests
import pprint
import json
import sys

provider = "http://localhost:8080"
consumer = "http://localhost:8081"

provider_alias = "http://provider-dataspace-connector"
consumer_alias = "http://consumer-dataspace-connector"

def main(argv):
    if(len(argv) == 2):
        provider_alias = argv[0]
        consumer_alias = argv[1]
        print("Setting provider alias as:", provider_alias)
        print("Setting consumer alias as:", consumer_alias)

if __name__ == "__main__":
    main(sys.argv[1:])

print("Starting script")

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()

s = requests.Session()
s.auth = ('admin', 'password')
s.verify = False

def create_catalog():
    response = s.post(provider + "/api/catalogs", json={})
    return response.headers['Location']


def create_offered_resource():
    response = s.post(provider + "/api/offers", json={})
    return response.headers['Location']


def create_representation():
    response = s.post(provider + "/api/representations", json={})
    return response.headers['Location']


def create_artifact():
    response = s.post(provider + "/api/artifacts", json={"value": "SOME LONG VALUE"})
    return response.headers['Location']


def create_contract():
    response = s.post(provider + "/api/contracts", json={'start': '2021-04-06T13:33:44.995+02:00', 'end':'2021-12-06T13:33:44.995+02:00'})
    return response.headers['Location']


def create_rule_allow_access():
    response = s.post(provider + "/api/rules", json={'value': """{
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
        }"""})
    return response.headers['Location']


def add_resource_to_catalog(catalog, resource):
    response = s.post(catalog + "/offers", json=[resource])

def add_catalog_to_resource(resource, catalog):
    response = s.post(resource + "/catalogs", json=[catalog])


def add_representation_to_resource(resource, representation):
    response = s.post(resource + "/representations",
                      json=[representation])


def add_artifact_to_representation(representation, artifact):
    response = s.post(representation + "/artifacts",
                      json=[artifact])


def add_contract_to_resource(resource, contract):
    response = s.post(resource + "/contracts", json=[contract])

def add_rule_to_contract(contract, rule):
    response = s.post(contract + "/rules", json=[rule])


# IDS

consumer_session = requests.Session()
consumer_session.auth = ('admin', 'password')
consumer_session.verify = False

def descriptionRequest(recipient, elementId):
    url = provider + "/api/ids/description"
    params = {}
    if recipient is not None:
        params['recipient'] = recipient
    if elementId is not None:
        params['elementId'] = elementId

    return consumer_session.post(url, params=params)


def contractRequest(recipient, resourceId, artifactId, download, contract):
    url = consumer + "/api/ids/contract"
    params = {}
    if recipient is not None:
        params['recipient'] = recipient
    if resourceId is not None:
        params['resourceIds'] = resourceId
    if artifactId is not None:
        params['artifactIds'] = artifactId
    if download is not None:
        params['download'] = download

    return consumer_session.post(url, params=params, json=[contract])

# Create resources
catalog = create_catalog()
offers = create_offered_resource()
representation = create_representation()
artifact = create_artifact()
contract = create_contract()
use_rule = create_rule_allow_access()

# Link resources
add_resource_to_catalog(catalog, offers)
add_representation_to_resource(offers, representation)
add_artifact_to_representation(representation, artifact)
add_contract_to_resource(offers, contract)
add_rule_to_contract(contract, use_rule)

# Replace localhost references
print(provider)
print(provider_alias)
offers = offers.replace(provider, provider_alias)
artifact = artifact.replace(provider, provider_alias)

print(offers)

# Call description
response = descriptionRequest(provider_alias + "/api/ids/data", offers)
offer = json.loads(response.text)
pprint.pprint(offer)

# Negotiate contract
obj = offer['ids:contractOffer'][0]['ids:permission'][0]
obj['ids:target'] = artifact
response = contractRequest(provider_alias + "/api/ids/data", offers, artifact, False, obj)
pprint.pprint(str(response.content))
