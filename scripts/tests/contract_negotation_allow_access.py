import requests
import pprint
import json
import tqdm

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()

s = requests.Session()
s.auth = ('admin', 'password')
s.verify = False

def create_catalog():
    response = s.post("https://localhost:8080/api/catalogs", json={})
    return response.headers['Location']


def create_offered_resource():
    response = s.post("https://localhost:8080/api/offers", json={})
    return response.headers['Location']


def create_representation():
    response = s.post("https://localhost:8080/api/representations", json={})
    return response.headers['Location']


def create_artifact():
    with open('img.dump' ,"r") as fp:
        response = s.post("https://localhost:8080/api/artifacts", json={"value": "SOME LONG VALUE"})
    return response.headers['Location']


def create_contract():
    response = s.post("https://localhost:8080/api/contracts", json={'start': '2021-04-06T13:33:44.995+02:00', 'end':'2021-12-06T13:33:44.995+02:00'})
    return response.headers['Location']


def create_rule_allow_access():
    response = s.post("https://localhost:8080/api/rules", json={'value': """{
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

# Reverse
def add_catalogs_to_resource(resource, catalogs):
    response = s.post(resource + "/catalogs", json=catalogs)

def add_resource_to_contract(contract, resources):
    response = s.post(contract + "/offers", json=resources)

def add_resource_to_representation(representation, resources):
    response = s.post(representation + "/offers", json=resources)

def add_representation_to_artifact(artifact, representations):
    response = s.post(artifact + "/representations", json=representations)

def add_contract_to_rules(rule, contracts):
    response = s.post(rule + "/contracts", json=contracts)


# IDS

def descriptionRequest(recipient, elementId):
    url = "https://localhost:8080/api/ids/description"
    params = {}
    if recipient is not None:
        params['recipient'] = recipient
    if elementId is not None:
        params['elementId'] = elementId

    return s.post(url, params=params)


def contractRequest(recipient, resourceId, artifactId, download, contract):
    url = "https://localhost:8080/api/ids/contract"
    params = {}
    if recipient is not None:
        params['recipient'] = recipient
    if resourceId is not None:
        params['resourceIds'] = resourceId
    if artifactId is not None:
        params['artifactIds'] = artifactId
    if download is not None:
        params['download'] = download

    return s.post(url, params=params, json=[contract])

# catalog1 = create_catalog()
# catalog2 = create_catalog()

# resource = create_offered_resource()
# add_catalogs_to_resource(resource, [catalog1, catalog2])

# pprint.pprint(s.get(catalog1 + "/offers").content)
# pprint.pprint(s.get(catalog2 + "/offers").content)

# pprint.pprint("-----------------------------------")

# resource1 = create_offered_resource()
# resource2 = create_offered_resource()
# contract = create_contract()

# add_resource_to_contract(contract, [resource1, resource2])

# pprint.pprint(s.get(resource1 + "/contracts").content)
# pprint.pprint(s.get(resource2 + "/contracts").content)

# pprint.pprint("-----------------------------------")

# resource1 = create_offered_resource()
# resource2 = create_offered_resource()
# representation = create_representation()

# add_resource_to_representation(representation, [resource1, resource2])

# pprint.pprint(s.get(resource1 + "/representations").content)
# pprint.pprint(s.get(resource2 + "/representations").content)

# pprint.pprint("-----------------------------------")

# representation1 = create_representation()
# representation2 = create_representation()
# artifact = create_artifact()

# add_representation_to_artifact(artifact, [representation1, representation2])

# pprint.pprint(s.get(representation1 + "/artifacts").content)
# pprint.pprint(s.get(representation2 + "/artifacts").content)

# pprint.pprint("-----------------------------------")

# contract1 = create_contract()
# contract2 = create_contract()
# rule = create_rule_allow_access()

# add_contract_to_rules(rule, [contract1, contract2])

# pprint.pprint(s.get(contract1 + "/rules").content)
# pprint.pprint(s.get(contract2 + "/rules").content)


# exit()

for i in tqdm.tqdm(range(1)):
    catalog = create_catalog()
    offers = create_offered_resource()
    representation = create_representation()
    artifact = create_artifact()
    contract = create_contract()
    use_rule = create_rule_allow_access()

    add_resource_to_catalog(catalog, offers)
    add_representation_to_resource(offers, representation)
    add_artifact_to_representation(representation, artifact)
    add_contract_to_resource(offers, contract)
    add_rule_to_contract(contract, use_rule)

# Call description
response = descriptionRequest("https://localhost:8090/api/ids/data", None)
pprint.pprint(str(response.content))
with open("description.json", "wb") as fp:
     fp.write(response.content)

response = descriptionRequest("https://localhost:8080/api/ids/data", offers)
pprint.pprint(str(response.content))
with open("offers.json", "wb") as fp:
    fp.write(response.content)

# response = descriptionRequest("https://localhost:8080/api/ids/data", artifact)
# pprint.pprint(str(response.content))
# with open("artifacts.json", "wb") as fp:
#     fp.write(response.content)

# response = descriptionRequest("https://localhost:8080/api/ids/data", contract)
# pprint.pprint(response.content)
# with open("contract.json", "wb") as fp:
#     fp.write(response.content)

# artifact = 'https://localhost:8080/api/artifacts/bd983695-8503-4225-b39f-081dcf6ced8e'
# contract = 'https://localhost:8080/api/contracts/84c7e314-c21e-4b08-b211-88da9873bdb8'

# with(open('index.jpeg', "rb")) as fp:
#     file = base64.b64encode(fp.read())
#     with(open('img.dump', "wb")) as fw:
#         fw.write(file)


# with open("index.jpeg", "rb") as fp:
#      s.put(artifact + "/data", data=fp.read())


with open("offers.json", "r") as fp:
    obj = json.load(fp)['ids:contractOffer'][0]['ids:permission'][0]
    obj['ids:target'] = artifact
    response = contractRequest(
        "https://localhost:8080/api/ids/data", offers, artifact, False, obj)
    pprint.pprint(str(response.content))


response = s.delete(offers)
pprint.pprint(str(response.content))
