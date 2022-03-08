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

import requests

# Suppress ssl verification warning
requests.packages.urllib3.disable_warnings()


class SubscriptionApi:
    session = None
    recipient = None

    def __init__(self, recipient, auth=("admin", "password")):
        self.session = requests.Session()
        self.session.auth = auth
        self.session.verify = False

        self.recipient = recipient

    def create_subscription(self, data={}):
        response = self.session.post(self.recipient + "/api/subscriptions", json=data)
        return response.headers["Location"]

    def subscription_message(self, data={}, params={}):
        response = self.session.post(
            self.recipient + "/api/ids/subscribe", json=data, params=params
        )
        return response

    def get_subscriptions(self):
        response = self.session.get(self.recipient + "/api/subscriptions")
        return response
