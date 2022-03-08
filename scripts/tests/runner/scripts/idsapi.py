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
import json

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()


class IdsApi:
    session = None
    recipient = None

    def __init__(self, recipient, auth=("admin", "password")):
        self.session = requests.Session()
        self.session.auth = auth
        self.session.verify = False

        self.recipient = recipient

    def descriptionRequest(self, recipient, elementId):
        url = self.recipient + "/api/ids/description"
        params = {}
        if recipient is not None:
            params["recipient"] = recipient
        if elementId is not None:
            params["elementId"] = elementId

        response = self.session.post(url, params=params)
        return json.loads(response.text)

    def contractRequest(self, recipient, resourceId, artifactId, download, contract):
        url = self.recipient + "/api/ids/contract"
        params = {}
        if recipient is not None:
            params["recipient"] = recipient
        if resourceId is not None:
            params["resourceIds"] = resourceId
        if artifactId is not None:
            params["artifactIds"] = artifactId
        if download is not None:
            params["download"] = download

        response = self.session.post(
            url, params=params, json=self.toListIfNeeded(contract)
        )
        return json.loads(response.text)

    def toListIfNeeded(self, obj):
        if isinstance(obj, list):
            return obj
        else:
            return [obj]
